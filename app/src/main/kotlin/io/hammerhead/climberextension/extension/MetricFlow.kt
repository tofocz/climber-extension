package io.hammerhead.climberextension.extension

import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnNavigationState
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

/**
 * Resolves any [Metric] - native, calculated, or climb-specific - to a formatted value
 * stream. Shared by Field 1 (Climb Info) and Field 2 (Quad Data) since CLAUDE.md's metric
 * list is selectable from both fields' pickers, so either field can be asked to render any
 * category, not just its "usual" ones.
 */
fun KarooSystemService.metricValueFlow(metric: Metric): Flow<String> {
    return when (metric) {
        Metric.DISTANCE_TO_TOP -> streamDataFlow(DataType.Type.DISTANCE_TO_TOP).map { formatMeters(it) }
        Metric.ELEV_TO_TOP -> streamDataFlow(DataType.Type.ELEVATION_TO_TOP).map { formatMeters(it) }
        Metric.AVG_CLIMB_GRADE -> combine(
            streamDataFlow(DataType.Type.CLIMB_NUMBER),
            consumerFlow<OnNavigationState>(),
        ) { climbNumberState, navigationState ->
            formatGrade(averageClimbGrade(climbNumberState, navigationState))
        }
        else -> when (metric.category) {
            Metric.Category.NATIVE -> metric.nativeTypeId?.let { nativeTypeId ->
                streamDataFlow(nativeTypeId).map { formatNative(it) }
            } ?: flowOf(MetricFallback.NOT_AVAILABLE)
            Metric.Category.CALCULATED, Metric.Category.CLIMB -> flowOf(MetricFallback.NOT_AVAILABLE)
        }
    }
}

private fun formatNative(state: StreamState): String {
    return (state as? StreamState.Streaming)?.dataPoint?.singleValue?.roundToInt()?.toString()
        ?: MetricFallback.NOT_AVAILABLE
}

private fun formatMeters(state: StreamState): String {
    val meters = (state as? StreamState.Streaming)?.dataPoint?.singleValue
        ?: return MetricFallback.NOT_AVAILABLE
    return meters.roundToInt().toString()
}

private fun formatGrade(grade: Double?): String {
    return grade?.let { "%.1f%%".format(it) } ?: MetricFallback.NOT_AVAILABLE
}

private fun averageClimbGrade(climbNumberState: StreamState, navigationState: OnNavigationState): Double? {
    val route = navigationState.state as? OnNavigationState.NavigationState.NavigatingRoute ?: return null
    val climbNumber = (climbNumberState as? StreamState.Streaming)
        ?.dataPoint
        ?.values
        ?.get(DataType.Field.CLIMB_NUMBER)
        ?.roundToInt()
        ?: return null
    return route.climbs.getOrNull(climbNumber - 1)?.grade
}

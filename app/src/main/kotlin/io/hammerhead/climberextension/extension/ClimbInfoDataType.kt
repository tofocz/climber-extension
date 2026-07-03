package io.hammerhead.climberextension.extension

import android.content.Context
import android.widget.RemoteViews
import io.hammerhead.climberextension.R
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnNavigationState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Field 1 - "Climb Info" (full-width, 3 cells): Distance to Top | Avg Climb Grade | Elev to Top.
 *
 * Distance/Elevation to Top come from ambient climb detection and populate without a route.
 * Avg Climb Grade only exists per-climb on a navigated route (CLAUDE.md: Field 1 behavior).
 */
class ClimbInfoDataType(
    private val karooSystem: KarooSystemService,
    extension: String,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            combine(
                karooSystem.streamDataFlow(DataType.Type.DISTANCE_TO_TOP),
                karooSystem.streamDataFlow(DataType.Type.ELEVATION_TO_TOP),
                karooSystem.streamDataFlow(DataType.Type.CLIMB_NUMBER),
                karooSystem.consumerFlow<OnNavigationState>(),
            ) { distanceToTop, elevationToTop, climbNumber, navigationState ->
                RemoteViews(context.packageName, R.layout.view_climb_info).apply {
                    setTextViewText(R.id.value_distance_to_top, formatMeters(distanceToTop.singleValueOrNull()))
                    setTextViewText(R.id.value_elev_to_top, formatMeters(elevationToTop.singleValueOrNull()))
                    setTextViewText(R.id.value_avg_grade, formatGrade(averageClimbGrade(climbNumber, navigationState)))
                }
            }.collect { remoteViews ->
                emitter.updateView(remoteViews)
            }
        }
        emitter.setCancellable {
            job.cancel()
        }
    }

    private fun StreamState.singleValueOrNull(): Double? {
        return (this as? StreamState.Streaming)?.dataPoint?.singleValue
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

    private fun formatMeters(meters: Double?): String {
        return meters?.roundToInt()?.toString() ?: ClimbInfoFallback.NO_CLIMB_DETECTED
    }

    private fun formatGrade(grade: Double?): String {
        return grade?.let { "%.1f%%".format(it) } ?: ClimbInfoFallback.NO_ROUTE
    }

    companion object {
        const val TYPE_ID = "climb-info"
    }
}

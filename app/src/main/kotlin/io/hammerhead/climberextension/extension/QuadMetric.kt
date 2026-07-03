package io.hammerhead.climberextension.extension

import io.hammerhead.karooext.models.DataType

/**
 * Metrics selectable per Quad Data cell (CLAUDE.md Field 2: native streams + calculated).
 *
 * Native metrics map directly to a [DataType.Type] stream. Calculated metrics have no
 * [nativeTypeId] - their math isn't implemented yet, see [CalculatedMetrics].
 */
enum class QuadMetric(val label: String, val nativeTypeId: String?) {
    POWER("Power", DataType.Type.POWER),
    HEART_RATE("HR", DataType.Type.HEART_RATE),
    CADENCE("Cadence", DataType.Type.CADENCE),
    SPEED("Speed", DataType.Type.SPEED),
    GRADE("Grade", DataType.Type.ELEVATION_GRADE),
    DISTANCE("Distance", DataType.Type.DISTANCE),

    WATTS_PER_KG("W/kg", nativeTypeId = null),
    VAM("VAM", nativeTypeId = null),
    INTENSITY_FACTOR("IF", nativeTypeId = null),
    NORMALIZED_POWER("NP", nativeTypeId = null),
    ENERGY_KJ("kJ", nativeTypeId = null),
    ;

    val isCalculated: Boolean
        get() = nativeTypeId == null
}

/**
 * Placeholder for calculated-metric math (W/kg, VAM, IF, NP, kJ).
 *
 * Formulas aren't implemented yet - cells configured with a calculated [QuadMetric] show
 * [PLACEHOLDER] until this is filled in.
 */
object CalculatedMetrics {
    const val PLACEHOLDER = "n/a"
}

/**
 * Fallback for a native Quad Data stream that isn't currently producing a value
 * (e.g. no sensor connected). Kept distinct from [CalculatedMetrics.PLACEHOLDER] and
 * [ClimbInfoFallback] since each covers a different "no data" case.
 */
object QuadDataFallback {
    const val NOT_STREAMING = "--"
}

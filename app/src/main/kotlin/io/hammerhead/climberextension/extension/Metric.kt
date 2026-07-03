package io.hammerhead.climberextension.extension

import io.hammerhead.karooext.models.DataType

/**
 * Shared metric list selectable in both Field 1 (Climb Info) and Field 2 (Quad Data)
 * pickers (CLAUDE.md v2: "single shared metric-picker component/config screen used by
 * both fields").
 *
 * [NATIVE] metrics map directly to a [DataType.Type] stream. [CALCULATED] metrics have no
 * formula implemented yet (see [MetricFallback]). [CLIMB] metrics are route/climb-detection
 * dependent and are resolved specially in [metricValueFlow].
 */
enum class Metric(val label: String, val category: Category, val nativeTypeId: String? = null) {
    POWER("Power", Category.NATIVE, DataType.Type.POWER),
    HEART_RATE("HR", Category.NATIVE, DataType.Type.HEART_RATE),
    CADENCE("Cadence", Category.NATIVE, DataType.Type.CADENCE),
    SPEED("Speed", Category.NATIVE, DataType.Type.SPEED),
    GRADE("Grade", Category.NATIVE, DataType.Type.ELEVATION_GRADE),
    DISTANCE("Distance", Category.NATIVE, DataType.Type.DISTANCE),

    WATTS_PER_KG("W/kg", Category.CALCULATED),
    VAM("VAM", Category.CALCULATED),
    INTENSITY_FACTOR("IF", Category.CALCULATED),
    NORMALIZED_POWER("NP", Category.CALCULATED),
    ENERGY_KJ("kJ", Category.CALCULATED),

    DISTANCE_TO_TOP("Dist to Top", Category.CLIMB),
    ELEV_TO_TOP("Elev to Top", Category.CLIMB),
    AVG_CLIMB_GRADE("Avg Grade", Category.CLIMB),
    ;

    enum class Category { NATIVE, CALCULATED, CLIMB }
}

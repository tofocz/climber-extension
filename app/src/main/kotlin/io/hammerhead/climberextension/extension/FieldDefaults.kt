package io.hammerhead.climberextension.extension

/**
 * Default per-cell metrics for each configurable field, used until a cell is customized
 * via the shared config screen.
 *
 * CLAUDE.md leaves Field 1's first-use behavior TBD ("no default/hardcoded assignment...
 * or ships with a sensible default selection") - this ships the sensible-default option,
 * carrying over the original fixed layout (Distance to Top | Avg Climb Grade | Elev to Top)
 * as the starting point rather than requiring configuration before any data shows.
 */
object FieldDefaults {
    val CLIMB_INFO = listOf(Metric.DISTANCE_TO_TOP, Metric.AVG_CLIMB_GRADE, Metric.ELEV_TO_TOP)
    val QUAD_DATA_1 = listOf(Metric.POWER, Metric.HEART_RATE, Metric.CADENCE, Metric.SPEED)
    val QUAD_DATA_2 = listOf(Metric.GRADE, Metric.DISTANCE, Metric.WATTS_PER_KG, Metric.VAM)

    fun quadData(instanceIndex: Int): List<Metric> = if (instanceIndex == 1) QUAD_DATA_1 else QUAD_DATA_2
}

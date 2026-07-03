package io.hammerhead.climberextension.extension

import android.content.Context

private const val PREFS_NAME = "quad_data_config"

private val DEFAULT_METRICS = mapOf(
    1 to listOf(QuadMetric.POWER, QuadMetric.HEART_RATE, QuadMetric.CADENCE, QuadMetric.SPEED),
    2 to listOf(QuadMetric.GRADE, QuadMetric.DISTANCE, QuadMetric.WATTS_PER_KG, QuadMetric.VAM),
)

/**
 * Persists the per-cell metric selection for each Quad Data instance (CLAUDE.md: two
 * instances needed, placed side by side in the drawer's bottom row).
 */
class QuadDataConfigStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getMetric(instanceIndex: Int, cellIndex: Int): QuadMetric {
        val stored = prefs.getString(keyFor(instanceIndex, cellIndex), null)
        val default = DEFAULT_METRICS.getValue(instanceIndex)[cellIndex]
        return stored?.let { name -> QuadMetric.entries.find { it.name == name } } ?: default
    }

    fun setMetric(instanceIndex: Int, cellIndex: Int, metric: QuadMetric) {
        prefs.edit().putString(keyFor(instanceIndex, cellIndex), metric.name).apply()
    }

    fun getAllMetrics(instanceIndex: Int): List<QuadMetric> {
        return DEFAULT_METRICS.getValue(instanceIndex).indices.map { getMetric(instanceIndex, it) }
    }

    private fun keyFor(instanceIndex: Int, cellIndex: Int) = "instance_${instanceIndex}_cell_$cellIndex"

    companion object {
        const val CELLS_PER_INSTANCE = 4
        val INSTANCE_INDICES = DEFAULT_METRICS.keys.toList()
    }
}

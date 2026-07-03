package io.hammerhead.climberextension.extension

import android.content.Context

private const val PREFS_NAME = "field_metric_config"

/**
 * Persists the per-cell metric selection for any configurable field. Keyed by [fieldKey]
 * (each field's [io.hammerhead.karooext.extension.DataTypeImpl.typeId]) plus cell index, so
 * Field 1's single "climb-info" instance and Field 2's two "quad-data" instances share one
 * picker/config-store mechanism instead of each field having its own (CLAUDE.md v2).
 */
class FieldMetricConfigStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getMetric(fieldKey: String, cellIndex: Int, default: Metric): Metric {
        val stored = prefs.getString(keyFor(fieldKey, cellIndex), null)
        return stored?.let { name -> Metric.entries.find { it.name == name } } ?: default
    }

    fun setMetric(fieldKey: String, cellIndex: Int, metric: Metric) {
        prefs.edit().putString(keyFor(fieldKey, cellIndex), metric.name).apply()
    }

    fun getAllMetrics(fieldKey: String, defaults: List<Metric>): List<Metric> {
        return defaults.indices.map { getMetric(fieldKey, it, defaults[it]) }
    }

    private fun keyFor(fieldKey: String, cellIndex: Int) = "${fieldKey}_cell_$cellIndex"
}

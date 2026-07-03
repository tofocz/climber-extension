package io.hammerhead.climberextension.extension

import android.content.Context
import android.widget.RemoteViews
import io.hammerhead.climberextension.R
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Field 1 - "Climb Info" (full-width, 3 cells), user-configurable via the same
 * [FieldMetricConfigStore] picker mechanism as Field 2's Quad Data (CLAUDE.md v2: single
 * shared metric-picker/config-store used by both fields - only the layout is fixed here).
 */
class ClimbInfoDataType(
    private val karooSystem: KarooSystemService,
    extension: String,
) : DataTypeImpl(extension, TYPE_ID) {

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val metrics = FieldMetricConfigStore(context).getAllMetrics(TYPE_ID, FieldDefaults.CLIMB_INFO)

        val job = CoroutineScope(Dispatchers.IO).launch {
            combine(
                karooSystem.metricValueFlow(metrics[0]),
                karooSystem.metricValueFlow(metrics[1]),
                karooSystem.metricValueFlow(metrics[2]),
            ) { value1, value2, value3 ->
                RemoteViews(context.packageName, R.layout.view_climb_info).apply {
                    setTextViewText(R.id.label_cell_1, metrics[0].label)
                    setTextViewText(R.id.value_cell_1, value1)
                    setTextViewText(R.id.label_cell_2, metrics[1].label)
                    setTextViewText(R.id.value_cell_2, value2)
                    setTextViewText(R.id.label_cell_3, metrics[2].label)
                    setTextViewText(R.id.value_cell_3, value3)
                }
            }.collect { remoteViews ->
                emitter.updateView(remoteViews)
            }
        }
        emitter.setCancellable {
            job.cancel()
        }
    }

    companion object {
        const val TYPE_ID = "climb-info"
    }
}

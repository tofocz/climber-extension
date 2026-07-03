package io.hammerhead.climberextension.extension

import android.content.Context
import android.widget.RemoteViews
import io.hammerhead.climberextension.R
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Field 2 - "Quad Data" (half-width, 2x2 grid of user-configurable cells).
 *
 * Two instances are registered ([instanceIndex] 1 and 2) so they sit side by side in the
 * drawer's bottom row, each reading its own per-cell metric selection from
 * [QuadDataConfigStore] (configured via the companion app's config screen).
 */
class QuadDataType(
    private val karooSystem: KarooSystemService,
    extension: String,
    private val instanceIndex: Int,
) : DataTypeImpl(extension, typeIdFor(instanceIndex)) {

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val metrics = QuadDataConfigStore(context).getAllMetrics(instanceIndex)

        val job = CoroutineScope(Dispatchers.IO).launch {
            combine(
                flowFor(metrics[0]),
                flowFor(metrics[1]),
                flowFor(metrics[2]),
                flowFor(metrics[3]),
            ) { value1, value2, value3, value4 ->
                RemoteViews(context.packageName, R.layout.view_quad_data).apply {
                    setTextViewText(R.id.label_cell_1, metrics[0].label)
                    setTextViewText(R.id.value_cell_1, value1)
                    setTextViewText(R.id.label_cell_2, metrics[1].label)
                    setTextViewText(R.id.value_cell_2, value2)
                    setTextViewText(R.id.label_cell_3, metrics[2].label)
                    setTextViewText(R.id.value_cell_3, value3)
                    setTextViewText(R.id.label_cell_4, metrics[3].label)
                    setTextViewText(R.id.value_cell_4, value4)
                }
            }.collect { remoteViews ->
                emitter.updateView(remoteViews)
            }
        }
        emitter.setCancellable {
            job.cancel()
        }
    }

    private fun flowFor(metric: QuadMetric): Flow<String> {
        val nativeTypeId = metric.nativeTypeId ?: return flowOf(CalculatedMetrics.PLACEHOLDER)
        return karooSystem.streamDataFlow(nativeTypeId).map { state ->
            (state as? StreamState.Streaming)?.dataPoint?.singleValue?.roundToInt()?.toString()
                ?: QuadDataFallback.NOT_STREAMING
        }
    }

    companion object {
        fun typeIdFor(instanceIndex: Int) = "quad-data-$instanceIndex"
    }
}

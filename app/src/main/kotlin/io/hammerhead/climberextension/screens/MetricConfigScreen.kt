package io.hammerhead.climberextension.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.hammerhead.climberextension.R
import io.hammerhead.climberextension.extension.FieldMetricConfigStore
import io.hammerhead.climberextension.extension.Metric

/**
 * Shared config screen for any field's metric picker (CLAUDE.md v2: single shared
 * metric-picker component/config screen used by both Field 1 and Field 2, just with a
 * different fixed cell count/layout per field).
 */
@Composable
fun MetricConfigScreen(
    title: String,
    fieldKey: String,
    defaults: List<Metric>,
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val store = remember(fieldKey) { FieldMetricConfigStore(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.onBackground)

        for (cellIndex in defaults.indices) {
            CellMetricSelector(
                label = stringResource(id = R.string.cell_label, cellIndex + 1),
                initialMetric = store.getMetric(fieldKey, cellIndex, defaults[cellIndex]),
                onMetricSelected = { metric -> store.setMetric(fieldKey, cellIndex, metric) },
            )
        }

        Button(onClick = onBack) {
            Text(text = stringResource(id = R.string.action_back))
        }
    }
}

@Composable
private fun CellMetricSelector(
    label: String,
    initialMetric: Metric,
    onMetricSelected: (Metric) -> Unit,
) {
    var selected by remember { mutableStateOf(initialMetric) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = true },
        ) {
            Text(text = "$label: ${selected.label}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Metric.entries.forEach { metric ->
                DropdownMenuItem(
                    text = { Text(metric.label) },
                    onClick = {
                        selected = metric
                        onMetricSelected(metric)
                        expanded = false
                    },
                )
            }
        }
    }
}

package io.hammerhead.climberextension

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.hammerhead.climberextension.extension.ClimbInfoDataType
import io.hammerhead.climberextension.extension.FieldDefaults
import io.hammerhead.climberextension.extension.Metric
import io.hammerhead.climberextension.extension.QuadDataType
import io.hammerhead.climberextension.screens.MainScreen
import io.hammerhead.climberextension.screens.MetricConfigScreen
import io.hammerhead.climberextension.theme.AppTheme

/**
 * The three configurable fields, sharing one config screen (CLAUDE.md v2). Each field's
 * [key] doubles as its [io.hammerhead.karooext.extension.DataTypeImpl.typeId] so config
 * lookups stay tied 1:1 with the data type that renders them.
 */
private enum class ConfigurableField(val key: String, val titleRes: Int, val defaults: List<Metric>) {
    CLIMB_INFO(ClimbInfoDataType.TYPE_ID, R.string.climb_info_display_name, FieldDefaults.CLIMB_INFO),
    QUAD_DATA_1(QuadDataType.typeIdFor(1), R.string.quad_data_1_display_name, FieldDefaults.QUAD_DATA_1),
    QUAD_DATA_2(QuadDataType.typeIdFor(2), R.string.quad_data_2_display_name, FieldDefaults.QUAD_DATA_2),
}

private sealed class ClimberScreen {
    data object Main : ClimberScreen()
    data class Config(val field: ConfigurableField) : ClimberScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                var screen by remember { mutableStateOf<ClimberScreen>(ClimberScreen.Main) }
                when (val current = screen) {
                    is ClimberScreen.Main -> MainScreen(
                        onConfigureClimbInfo = { screen = ClimberScreen.Config(ConfigurableField.CLIMB_INFO) },
                        onConfigureQuadData = { instanceIndex ->
                            val field = if (instanceIndex == 1) ConfigurableField.QUAD_DATA_1 else ConfigurableField.QUAD_DATA_2
                            screen = ClimberScreen.Config(field)
                        },
                    )
                    is ClimberScreen.Config -> MetricConfigScreen(
                        title = stringResource(id = current.field.titleRes),
                        fieldKey = current.field.key,
                        defaults = current.field.defaults,
                        onBack = { screen = ClimberScreen.Main },
                    )
                }
            }
        }
    }
}

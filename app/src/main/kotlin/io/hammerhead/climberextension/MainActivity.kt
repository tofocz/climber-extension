package io.hammerhead.climberextension

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.hammerhead.climberextension.screens.MainScreen
import io.hammerhead.climberextension.screens.QuadDataConfigScreen
import io.hammerhead.climberextension.theme.AppTheme

private sealed class ClimberScreen {
    data object Main : ClimberScreen()
    data class QuadDataConfig(val instanceIndex: Int) : ClimberScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                var screen by remember { mutableStateOf<ClimberScreen>(ClimberScreen.Main) }
                when (val current = screen) {
                    is ClimberScreen.Main -> MainScreen(
                        onConfigureQuadData = { instanceIndex -> screen = ClimberScreen.QuadDataConfig(instanceIndex) },
                    )
                    is ClimberScreen.QuadDataConfig -> QuadDataConfigScreen(
                        instanceIndex = current.instanceIndex,
                        onBack = { screen = ClimberScreen.Main },
                    )
                }
            }
        }
    }
}

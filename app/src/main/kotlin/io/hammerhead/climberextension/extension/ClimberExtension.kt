package io.hammerhead.climberextension.extension

import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.KarooExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClimberExtension : KarooExtension(EXTENSION_ID, "1.0") {
    private val karooSystem: KarooSystemService by lazy { KarooSystemService(this) }

    private var serviceJob: Job? = null

    override val types by lazy {
        listOf(
            ClimbInfoDataType(karooSystem, extension),
            QuadDataType(karooSystem, extension, instanceIndex = 1),
            QuadDataType(karooSystem, extension, instanceIndex = 2),
        )
    }

    override fun onCreate() {
        super.onCreate()
        serviceJob = CoroutineScope(Dispatchers.IO).launch {
            karooSystem.connect { /* no-op on connect for now */ }
        }
    }

    override fun onDestroy() {
        serviceJob?.cancel()
        serviceJob = null
        karooSystem.disconnect()
        super.onDestroy()
    }

    companion object {
        const val EXTENSION_ID = "climber"
    }
}

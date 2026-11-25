package com.example.safeher.services

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.*

class SirenService(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private var isPlaying = false
    private var sirenJob: Job? = null

    fun startSiren() {
        if (isPlaying) return
        isPlaying = true

        sirenJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                while (isPlaying && isActive) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1000)
                    delay(1200)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun stopSiren() {
        isPlaying = false
        sirenJob?.cancel()
        try {
            toneGenerator?.stopTone()
            toneGenerator?.release()
        } catch (_: Exception) {
        }
        toneGenerator = null
    }
}

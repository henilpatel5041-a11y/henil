package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural synthesizer for instant, zero-latency calming tactile audio effects.
 * Requires no external audio assets or network downloads.
 */
class TactileAudioPlayer {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 22050

    fun playBubblePop(pitchMultiplier: Float = 1.0f) {
        scope.launch {
            try {
                val durationMs = 45
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                val baseFreq = 540.0 * pitchMultiplier

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Frequency glide downward creates the classic acoustic bubble "plop"
                    val currentFreq = baseFreq * (1.0 - (t / 0.050) * 0.45)
                    val envelope = exp(-t * 90.0) // snappy acoustic decay
                    val sample = sin(2.0 * PI * currentFreq * t) * envelope
                    buffer[i] = (sample * 18000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playBuffer(buffer)
            } catch (_: Exception) {
                // Graceful fallback
            }
        }
    }

    fun playWaterDrop() {
        scope.launch {
            try {
                val durationMs = 80
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Frequency rise then fall creates a water droplet sound
                    val freq = 750.0 + 350.0 * sin(PI * (t / 0.080))
                    val envelope = exp(-t * 45.0)
                    val sample = sin(2.0 * PI * freq * t) * envelope
                    buffer[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playBuffer(buffer)
            } catch (_: Exception) {
            }
        }
    }

    fun playSquish() {
        scope.launch {
            try {
                val durationMs = 60
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val freq = 320.0 + 260.0 * sin(PI * (t / 0.060))
                    val env = exp(-t * 50.0)
                    val sample = sin(2.0 * PI * freq * t) * env
                    buffer[i] = (sample * 17000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playBuffer(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playStoneClink() {
        scope.launch {
            try {
                val durationMs = 120
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 85.0)
                    val s1 = sin(2.0 * PI * 880.0 * t) * 0.7
                    val s2 = sin(2.0 * PI * 1320.0 * t) * 0.3
                    val sample = (s1 + s2) * env
                    buffer[i] = (sample * 19000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playBuffer(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playSingingBowlChime(fundamentalHz: Double = 432.0) {
        scope.launch {
            try {
                val durationMs = 1200
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env1 = exp(-t * 2.2)
                    val env2 = exp(-t * 3.8)
                    val env3 = exp(-t * 5.0)

                    // Harmonics: 1x, 2.76x (tibetan bowl overtone ratio), 5.4x
                    val wave1 = sin(2.0 * PI * fundamentalHz * t) * env1 * 0.65
                    val wave2 = sin(2.0 * PI * (fundamentalHz * 2.76) * t) * env2 * 0.25
                    val wave3 = sin(2.0 * PI * (fundamentalHz * 5.4) * t) * env3 * 0.10

                    val sample = wave1 + wave2 + wave3
                    buffer[i] = (sample * 22000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playBuffer(buffer)
            } catch (_: Exception) {
            }
        }
    }

    fun playBreathChime(isExhale: Boolean) {
        scope.launch {
            try {
                val durationMs = 600
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                val freq = if (isExhale) 384.0 else 528.0 // 528 Hz is the transformation/calm frequency

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val envelope = exp(-t * 4.5)
                    val sample = (sin(2.0 * PI * freq * t) * 0.8 + sin(2.0 * PI * freq * 2.0 * t) * 0.2) * envelope
                    buffer[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playBuffer(buffer)
            } catch (_: Exception) {
            }
        }
    }

    private fun playBuffer(buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()

        // Schedule cleanup after buffer ends
        scope.launch {
            kotlinx.coroutines.delay((buffer.size * 1000L / sampleRate) + 200L)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}

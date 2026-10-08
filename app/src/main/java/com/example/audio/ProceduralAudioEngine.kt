package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.sin

data class SoundscapeState(
    val isPlaying: Boolean = false,
    val rainVolume: Float = 0.5f,
    val oceanVolume: Float = 0.4f,
    val omDroneVolume: Float = 0.3f,
    val cricketsVolume: Float = 0.2f,
    val masterVolume: Float = 0.7f,
    val timerRemainingSeconds: Int = 0, // 0 means continuous / no timer
    val droneFrequencyHz: Double = 432.0,
    val droneLabel: String = "432 Hz Nature Calm"
)

class ProceduralAudioEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var playbackJob: Job? = null
    private var timerJob: Job? = null
    private val sampleRate = 22050

    private val _state = MutableStateFlow(SoundscapeState())
    val state: StateFlow<SoundscapeState> = _state.asStateFlow()

    private val random = Random()

    fun setDroneFrequency(hz: Double, label: String) {
        _state.value = _state.value.copy(
            droneFrequencyHz = hz,
            droneLabel = label
        )
    }

    fun updateVolumes(
        rain: Float = _state.value.rainVolume,
        ocean: Float = _state.value.oceanVolume,
        omDrone: Float = _state.value.omDroneVolume,
        crickets: Float = _state.value.cricketsVolume,
        master: Float = _state.value.masterVolume
    ) {
        _state.value = _state.value.copy(
            rainVolume = rain.coerceIn(0f, 1f),
            oceanVolume = ocean.coerceIn(0f, 1f),
            omDroneVolume = omDrone.coerceIn(0f, 1f),
            cricketsVolume = crickets.coerceIn(0f, 1f),
            masterVolume = master.coerceIn(0f, 1f)
        )
    }

    fun setTimer(minutes: Int) {
        timerJob?.cancel()
        val seconds = minutes * 60
        _state.value = _state.value.copy(timerRemainingSeconds = seconds)
        if (seconds > 0 && _state.value.isPlaying) {
            startTimerCountdown()
        }
    }

    private fun startTimerCountdown() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (_state.value.timerRemainingSeconds > 0 && _state.value.isPlaying) {
                delay(1000L)
                val remaining = _state.value.timerRemainingSeconds - 1
                _state.value = _state.value.copy(timerRemainingSeconds = remaining)
                if (remaining <= 0) {
                    stop()
                    break
                }
            }
        }
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            stop()
        } else {
            play()
        }
    }

    fun play() {
        if (_state.value.isPlaying) return
        _state.value = _state.value.copy(isPlaying = true)
        if (_state.value.timerRemainingSeconds > 0) {
            startTimerCountdown()
        }

        playbackJob?.cancel()
        playbackJob = scope.launch(Dispatchers.Default) {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBufferSize.coerceAtLeast(4096)

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                val audioBuffer = ShortArray(1024)
                var timeIndex = 0L

                // Pink noise filter state for rain
                var b0 = 0.0
                var b1 = 0.0
                var b2 = 0.0
                var oceanPhase = 0.0

                while (isActive && _state.value.isPlaying) {
                    val currentState = _state.value
                    val rainVol = currentState.rainVolume * currentState.masterVolume
                    val oceanVol = currentState.oceanVolume * currentState.masterVolume
                    val droneVol = currentState.omDroneVolume * currentState.masterVolume
                    val cricketsVol = currentState.cricketsVolume * currentState.masterVolume

                    for (i in audioBuffer.indices) {
                        val t = (timeIndex + i).toDouble() / sampleRate
                        var sample = 0.0

                        // 1. Procedural Gentle Rain (Filtered Pink Noise with soft drops)
                        if (rainVol > 0.01f) {
                            val white = (random.nextDouble() * 2.0 - 1.0)
                            b0 = 0.99765 * b0 + white * 0.0990460
                            b1 = 0.96300 * b1 + white * 0.2965164
                            b2 = 0.57000 * b2 + white * 1.0526913
                            val pink = (b0 + b1 + b2 + white * 0.1848) * 0.08
                            // Occasional soft drip
                            val drip = if (random.nextInt(1800) == 0) (random.nextDouble() * 0.4) else 0.0
                            sample += (pink + drip) * rainVol
                        }

                        // 2. Procedural Ocean Waves (Wave swells: 7-second period)
                        if (oceanVol > 0.01f) {
                            oceanPhase += (2.0 * PI / (sampleRate * 7.5))
                            if (oceanPhase > 2.0 * PI) oceanPhase -= 2.0 * PI
                            val swell = (sin(oceanPhase) * 0.5 + 0.5) // 0.0 to 1.0
                            val surfWhite = (random.nextDouble() * 2.0 - 1.0) * (swell * swell)
                            sample += surfWhite * 0.18 * oceanVol
                        }

                        // 3. Procedural Solfeggio / Singing Meditation Drone
                        if (droneVol > 0.01f) {
                            val droneFreq = currentState.droneFrequencyHz
                            val tremolo = 0.85 + 0.15 * sin(2.0 * PI * 0.2 * t)
                            val fundamental = sin(2.0 * PI * droneFreq * t)
                            val octave = sin(2.0 * PI * (droneFreq * 0.5) * t) * 0.5
                            val third = sin(2.0 * PI * (droneFreq * 2.0) * t) * 0.2
                            sample += (fundamental + octave + third) * 0.14 * tremolo * droneVol
                        }

                        // 4. Procedural Night Crickets & Forest Breeze
                        if (cricketsVol > 0.01f) {
                            // Chirp pulses at ~4500Hz gated by 4Hz rhythm
                            val chirpGate = sin(2.0 * PI * 4.0 * t)
                            val cricketWave = if (chirpGate > 0.7) {
                                sin(2.0 * PI * 4600.0 * t) * (chirpGate - 0.7) * 0.4
                            } else {
                                0.0
                            }
                            // gentle wind low frequency
                            val wind = sin(2.0 * PI * 0.12 * t) * (random.nextDouble() * 0.05)
                            sample += (cricketWave + wind) * 0.22 * cricketsVol
                        }

                        audioBuffer[i] = (sample * 30000.0).toInt().coerceIn(-32767, 32767).toShort()
                    }

                    timeIndex += audioBuffer.size
                    track.write(audioBuffer, 0, audioBuffer.size)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun stop() {
        _state.value = _state.value.copy(isPlaying = false)
        playbackJob?.cancel()
        playbackJob = null
        timerJob?.cancel()
        timerJob = null
    }

    fun release() {
        stop()
    }
}

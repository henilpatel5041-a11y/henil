package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.ProceduralAudioEngine
import com.example.audio.SoundscapeState
import com.example.audio.TactileAudioPlayer
import com.example.data.MindfulSession
import com.example.data.SerenityDatabase
import com.example.data.SerenityRepository
import com.example.data.StressCheckIn
import com.example.util.HapticHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SerenityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SerenityRepository
    val tactileAudio = TactileAudioPlayer()
    val haptic = HapticHelper(application.applicationContext)
    val audioEngine = ProceduralAudioEngine()

    val checkIns: StateFlow<List<StressCheckIn>>
    val sessions: StateFlow<List<MindfulSession>>
    val totalMindfulSeconds: StateFlow<Int?>

    val soundscapeState: StateFlow<SoundscapeState> = audioEngine.state

    // Daily Affirmations list
    val affirmations = listOf(
        "Peace begins with a conscious breath.",
        "You don’t have to control your thoughts. Just notice them pass.",
        "In this present moment, you are safe and supported.",
        "Release what you cannot control. Embrace what brings you peace.",
        "Gentleness with yourself is strength.",
        "Like water over river stones, let tension smooth away.",
        "Slow down. The universe is not in a rush."
    )

    private val _currentAffirmation = MutableStateFlow(affirmations.first())
    val currentAffirmation: StateFlow<String> = _currentAffirmation.asStateFlow()

    init {
        val dao = SerenityDatabase.getDatabase(application).serenityDao()
        repository = SerenityRepository(dao)

        checkIns = repository.allCheckIns.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        sessions = repository.allSessions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        totalMindfulSeconds = repository.totalMindfulSeconds.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

        _currentAffirmation.value = affirmations.random()
    }

    fun nextAffirmation() {
        haptic.gentlePulse()
        _currentAffirmation.value = affirmations.random()
    }

    // Audio & Haptic triggers
    fun onBubblePopped(pitch: Float = 1.0f) {
        haptic.pop()
        tactileAudio.playBubblePop(pitch)
    }

    fun onWaterRippled() {
        haptic.gentlePulse()
        tactileAudio.playWaterDrop()
    }

    fun onSingingBowlStruck() {
        haptic.chimePulse()
        tactileAudio.playSingingBowlChime()
    }

    fun onSingingBowlResonated() {
        haptic.resonate()
    }

    fun onSquish() {
        haptic.resonate()
        tactileAudio.playSquish()
    }

    fun onStoneClink() {
        haptic.pop()
        tactileAudio.playStoneClink()
    }

    fun setDroneFrequency(hz: Double, label: String) {
        haptic.gentlePulse()
        audioEngine.setDroneFrequency(hz, label)
    }

    fun onBreathPhaseChanged(isExhale: Boolean) {
        haptic.gentlePulse()
        tactileAudio.playBreathChime(isExhale)
    }

    // Database Actions
    fun recordCheckIn(stressLevel: Int, mood: String, note: String) {
        viewModelScope.launch {
            repository.saveCheckIn(
                StressCheckIn(
                    stressLevel = stressLevel,
                    mood = mood,
                    note = note
                )
            )
            haptic.chimePulse()
        }
    }

    fun deleteCheckIn(checkIn: StressCheckIn) {
        viewModelScope.launch {
            repository.removeCheckIn(checkIn)
        }
    }

    fun recordSession(type: String, durationSeconds: Int, cycles: Int = 0) {
        viewModelScope.launch {
            repository.saveSession(
                MindfulSession(
                    sessionType = type,
                    durationSeconds = durationSeconds,
                    cyclesCompleted = cycles
                )
            )
        }
    }

    // Soundscape controls
    fun toggleSoundscape() {
        haptic.gentlePulse()
        audioEngine.togglePlayPause()
    }

    fun updateSoundscapeVolumes(
        rain: Float = soundscapeState.value.rainVolume,
        ocean: Float = soundscapeState.value.oceanVolume,
        om: Float = soundscapeState.value.omDroneVolume,
        crickets: Float = soundscapeState.value.cricketsVolume,
        master: Float = soundscapeState.value.masterVolume
    ) {
        audioEngine.updateVolumes(rain, ocean, om, crickets, master)
    }

    fun setSoundscapeTimer(minutes: Int) {
        haptic.gentlePulse()
        audioEngine.setTimer(minutes)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}

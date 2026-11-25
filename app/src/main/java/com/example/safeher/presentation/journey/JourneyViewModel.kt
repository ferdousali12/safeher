package com.example.safeher.presentation.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeher.data.model.Journey
import com.example.safeher.data.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class JourneyViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val firestore = FirebaseFirestore.getInstance()

    private val _journeyActive = MutableStateFlow(false)
    val journeyActive: StateFlow<Boolean> = _journeyActive.asStateFlow()

    private val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _timerActive = MutableStateFlow(false)
    val timerActive: StateFlow<Boolean> = _timerActive.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(0)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _timerExpiredPrompt = MutableStateFlow(false)
    val timerExpiredPrompt: StateFlow<Boolean> = _timerExpiredPrompt.asStateFlow()

    private var timerJob: Job? = null

    fun startJourney(dest: String, durationMinutes: Int) {
        if (dest.isBlank()) return
        _destination.value = dest
        _journeyActive.value = true

        val userId = authRepository.currentUser?.uid ?: "anonymous"
        viewModelScope.launch {
            try {
                val journey = Journey(
                    userId = userId,
                    destination = dest,
                    startTime = System.currentTimeMillis(),
                    expectedDurationMinutes = durationMinutes,
                    status = "IN_PROGRESS"
                )
                val docRef = firestore.collection("safe_journeys").document()
                val newJourney = journey.copy(id = docRef.id)
                docRef.set(newJourney.toMap()).await()
            } catch (_: Exception) {
            }
        }
    }

    fun endJourney() {
        _journeyActive.value = false
        _destination.value = ""
    }

    fun startTimer(minutes: Int) {
        if (minutes <= 0) return
        _timerSecondsRemaining.value = minutes * 60
        _timerActive.value = true
        _timerExpiredPrompt.value = false

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                delay(1000)
                _timerSecondsRemaining.value -= 1
            }
            _timerActive.value = false
            _timerExpiredPrompt.value = true
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _timerActive.value = false
        _timerExpiredPrompt.value = false
        _timerSecondsRemaining.value = 0
    }

    fun confirmSafe() {
        _timerExpiredPrompt.value = false
    }
}

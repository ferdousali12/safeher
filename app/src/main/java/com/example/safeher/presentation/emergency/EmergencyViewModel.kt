package com.example.safeher.presentation.emergency

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeher.data.model.Alert
import com.example.safeher.data.repository.AuthRepository
import com.example.safeher.services.LocationService
import com.example.safeher.services.SirenService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EmergencyViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val firestore = FirebaseFirestore.getInstance()

    private val _sosState = MutableStateFlow<SOSState>(SOSState.Idle)
    val sosState: StateFlow<SOSState> = _sosState.asStateFlow()

    private val _countdown = MutableStateFlow(5)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    private val _emergencyHistory = MutableStateFlow<List<Alert>>(emptyList())
    val emergencyHistory: StateFlow<List<Alert>> = _emergencyHistory.asStateFlow()

    private val _sirenActive = MutableStateFlow(false)
    val sirenActive: StateFlow<Boolean> = _sirenActive.asStateFlow()

    private var countdownJob: Job? = null
    private var sirenService: SirenService? = null

    init {
        loadEmergencyHistory()
    }

    fun startCountdown(context: Context) {
        if (_sosState.value is SOSState.Countdown || _sosState.value is SOSState.Active) return
        _sosState.value = SOSState.Countdown
        _countdown.value = 5

        toggleSiren(context, true)

        countdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                _countdown.value = i
                delay(1000)
            }
            triggerSOS(context)
        }
    }

    fun cancelCountdown(context: Context) {
        countdownJob?.cancel()
        toggleSiren(context, false)
        _sosState.value = SOSState.Idle
    }

    fun triggerSOS(context: Context? = null) {
        countdownJob?.cancel()
        _sosState.value = SOSState.Active

        val userId = authRepository.currentUser?.uid ?: "anonymous"

        viewModelScope.launch {
            var lat = 0.0
            var lng = 0.0
            if (context != null) {
                val locService = LocationService(context)
                val loc = locService.getCurrentLocation()
                if (loc != null) {
                    lat = loc.latitude
                    lng = loc.longitude
                }
                toggleSiren(context, true)
            }

            val mapsUrl = "https://maps.google.com/?q=$lat,$lng"
            val alert = Alert(
                userId = userId,
                latitude = lat,
                longitude = lng,
                timestamp = System.currentTimeMillis(),
                status = "ACTIVE"
            )

            try {
                val docRef = firestore.collection("emergency_alerts").document()
                val newAlert = alert.copy(id = docRef.id)
                docRef.set(newAlert.toMap()).await()
                loadEmergencyHistory()
            } catch (_: Exception) {
            }

            if (context != null) {
                sendSmsToContacts(context, mapsUrl)
            }
        }
    }

    fun toggleSiren(context: Context, activate: Boolean) {
        if (sirenService == null) {
            sirenService = SirenService(context)
        }
        if (activate) {
            sirenService?.startSiren()
            _sirenActive.value = true
        } else {
            sirenService?.stopSiren()
            _sirenActive.value = false
        }
    }

    private fun sendSmsToContacts(context: Context, mapsUrl: String) {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("contacts")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("isPreferred", true)
                    .get()
                    .await()

                val phoneNumbers = snapshot.documents.mapNotNull { it.getString("phoneNumber") }
                if (phoneNumbers.isNotEmpty()) {
                    val message = "EMERGENCY ALERT! I need immediate assistance. My location: $mapsUrl"
                    val recipients = phoneNumbers.joinToString(";")
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("smsto:$recipients")
                        putExtra("sms_body", message)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun loadEmergencyHistory() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("emergency_alerts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                val alerts = snapshot.documents.mapNotNull { it.toObject(Alert::class.java) }
                _emergencyHistory.value = alerts.sortedByDescending { it.timestamp }
            } catch (_: Exception) {
            }
        }
    }

    fun cancelSOS(context: Context) {
        toggleSiren(context, false)
        _sosState.value = SOSState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        sirenService?.stopSiren()
    }

    sealed class SOSState {
        object Idle : SOSState()
        object Countdown : SOSState()
        object Active : SOSState()
    }
}

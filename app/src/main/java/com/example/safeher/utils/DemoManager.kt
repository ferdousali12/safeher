package com.example.safeher.utils

import com.example.safeher.data.model.Alert
import com.example.safeher.data.model.Contact
import com.example.safeher.data.model.Journey
import com.example.safeher.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DemoManager {
    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    val demoUser = User(
        uid = "demo_user_123",
        fullName = "Ayesha Khan",
        email = "ayesha.demo@example.com",
        phoneNumber = "03001234567"
    )

    private val _demoContacts = MutableStateFlow(
        listOf(
            Contact(id = "demo_1", userId = "demo_user_123", name = "Amna Khan", phoneNumber = "03009876543", relationship = "Sister", isPreferred = true),
            Contact(id = "demo_2", userId = "demo_user_123", name = "Sara Ahmed", phoneNumber = "03215551234", relationship = "Friend", isPreferred = false),
            Contact(id = "demo_3", userId = "demo_user_123", name = "Fatima Ali", phoneNumber = "03334445555", relationship = "Mother", isPreferred = true)
        )
    )
    val demoContacts: StateFlow<List<Contact>> = _demoContacts.asStateFlow()

    private val _demoAlerts = MutableStateFlow(
        listOf(
            Alert(id = "demo_alert_1", userId = "demo_user_123", latitude = 31.5204, longitude = 74.3587, timestamp = System.currentTimeMillis() - 3600000, status = "SOS Test Alert – Completed"),
            Alert(id = "demo_alert_2", userId = "demo_user_123", latitude = 31.5204, longitude = 74.3587, timestamp = System.currentTimeMillis() - 172800000, status = "Location Test – Successful"),
            Alert(id = "demo_alert_3", userId = "demo_user_123", latitude = 31.5204, longitude = 74.3587, timestamp = System.currentTimeMillis() - 432000000, status = "Safety Check-in – Completed")
        )
    )
    val demoAlerts: StateFlow<List<Alert>> = _demoAlerts.asStateFlow()

    private val _demoJourney = MutableStateFlow(
        Journey(
            id = "demo_journey_1",
            userId = "demo_user_123",
            destination = "Home (from University Campus)",
            startTime = System.currentTimeMillis() - 600000,
            expectedDurationMinutes = 30,
            status = "In Progress"
        )
    )
    val demoJourney: StateFlow<Journey> = _demoJourney.asStateFlow()

    fun setDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
    }

    fun addDemoContact(contact: Contact) {
        val newContact = contact.copy(id = "demo_${System.currentTimeMillis()}")
        _demoContacts.value = _demoContacts.value + newContact
    }

    fun deleteDemoContact(id: String) {
        _demoContacts.value = _demoContacts.value.filter { it.id != id }
    }

    fun addDemoAlert(alert: Alert) {
        val newAlert = alert.copy(id = "demo_alert_${System.currentTimeMillis()}")
        _demoAlerts.value = listOf(newAlert) + _demoAlerts.value
    }
}

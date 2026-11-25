package com.example.safeher.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeher.data.model.Contact
import com.example.safeher.data.repository.AuthRepository
import com.example.safeher.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val contactRepository = ContactRepository()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = contactRepository.getContacts(userId)
            if (result.isSuccess) {
                _contacts.value = result.getOrDefault(emptyList())
            }
        }
    }

    fun addContact(name: String, phone: String, relationship: String, isPreferred: Boolean, onComplete: () -> Unit) {
        val userId = authRepository.currentUser?.uid ?: return
        if (name.isBlank() || phone.isBlank()) {
            _errorMessage.value = "Name and phone number cannot be blank."
            return
        }

        viewModelScope.launch {
            _errorMessage.value = null
            val contact = Contact(userId = userId, name = name, phoneNumber = phone, relationship = relationship, isPreferred = isPreferred)
            val result = contactRepository.addContact(contact)
            if (result.isSuccess) {
                loadContacts()
                onComplete()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to add contact."
            }
        }
    }

    fun updateContact(contact: Contact, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = contactRepository.updateContact(contact)
            if (result.isSuccess) {
                loadContacts()
                onComplete()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to update contact."
            }
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            contactRepository.deleteContact(contactId)
            loadContacts()
        }
    }
}

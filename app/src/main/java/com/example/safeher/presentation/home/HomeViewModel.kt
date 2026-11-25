package com.example.safeher.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeher.data.model.User
import com.example.safeher.data.repository.AuthRepository
import com.example.safeher.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val firebaseUser = authRepository.currentUser
        if (firebaseUser != null) {
            viewModelScope.launch {
                val result = userRepository.getUser(firebaseUser.uid)
                if (result.isSuccess) {
                    _user.value = result.getOrNull()
                }
            }
        }
    }
}

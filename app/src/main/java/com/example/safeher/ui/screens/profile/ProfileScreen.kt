package com.example.safeher.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safeher.components.SafeHerButton
import com.example.safeher.presentation.auth.AuthViewModel
import com.example.safeher.presentation.profile.ProfileViewModel
import com.example.safeher.utils.DemoManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val isDemoMode by DemoManager.isDemoMode.collectAsState()
    val realUser by profileViewModel.user.collectAsState()
    val user = if (isDemoMode) DemoManager.demoUser else realUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isDemoMode) "Demo Profile" else "User Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = user?.fullName ?: "Loading...", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = user?.email ?: "", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user?.phoneNumber ?: "", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))

                if (isDemoMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Fictional Demo Profile",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            SafeHerButton(
                text = if (isDemoMode) "Exit Demo Mode" else "Log Out",
                onClick = {
                    if (isDemoMode) {
                        DemoManager.setDemoMode(false)
                        onLogout()
                    } else {
                        authViewModel.logout(onLogout)
                    }
                },
                isDestructive = true
            )
        }
    }
}

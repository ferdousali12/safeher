package com.example.safeher.ui.screens.journey

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
import com.example.safeher.components.SafeHerTextField
import com.example.safeher.presentation.journey.JourneyViewModel
import com.example.safeher.ui.theme.EmergencyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeJourneyScreen(
    onBack: () -> Unit,
    viewModel: JourneyViewModel = viewModel()
) {
    val journeyActive by viewModel.journeyActive.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val timerActive by viewModel.timerActive.collectAsState()
    val timerSeconds by viewModel.timerSecondsRemaining.collectAsState()
    val timerExpiredPrompt by viewModel.timerExpiredPrompt.collectAsState()

    var destInput by remember { mutableStateOf("") }
    var durationInput by remember { mutableStateOf("30") }
    var timerMinutesInput by remember { mutableStateOf("15") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safe Journey & Timer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Safe Journey Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Safe Journey Tracker", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!journeyActive) {
                        SafeHerTextField(value = destInput, onValueChange = { destInput = it }, label = "Destination")
                        Spacer(modifier = Modifier.height(8.dp))
                        SafeHerTextField(value = durationInput, onValueChange = { durationInput = it }, label = "Expected Duration (minutes)")
                        Spacer(modifier = Modifier.height(16.dp))
                        SafeHerButton(
                            text = "Start Journey",
                            onClick = {
                                val duration = durationInput.toIntOrNull() ?: 30
                                viewModel.startJourney(destInput, duration)
                            }
                        )
                    } else {
                        Text("Journey in Progress to: $destination", color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        SafeHerButton(
                            text = "I've Arrived / End Journey",
                            onClick = { viewModel.endJourney() }
                        )
                    }
                }
            }

            // Safety Timer Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Safety Check-In Timer", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!timerActive && !timerExpiredPrompt) {
                        SafeHerTextField(value = timerMinutesInput, onValueChange = { timerMinutesInput = it }, label = "Timer Duration (minutes)")
                        Spacer(modifier = Modifier.height(16.dp))
                        SafeHerButton(
                            text = "Start Timer",
                            onClick = {
                                val mins = timerMinutesInput.toIntOrNull() ?: 15
                                viewModel.startTimer(mins)
                            }
                        )
                    } else if (timerActive) {
                        val mins = timerSeconds / 60
                        val secs = timerSeconds % 60
                        Text(text = "Time Remaining: %02d:%02d".format(mins, secs), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        SafeHerButton(
                            text = "Cancel Timer",
                            onClick = { viewModel.cancelTimer() }
                        )
                    } else if (timerExpiredPrompt) {
                        Text(text = "Timer Expired! Are you safe?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmergencyRed)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { viewModel.confirmSafe() }, modifier = Modifier.weight(1f)) {
                                Text("I'm Safe")
                            }
                        }
                    }
                }
            }
        }
    }
}

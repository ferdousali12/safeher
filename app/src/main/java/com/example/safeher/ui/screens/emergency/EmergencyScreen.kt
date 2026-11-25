package com.example.safeher.ui.screens.emergency

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safeher.components.SafeHerButton
import com.example.safeher.data.model.Alert
import com.example.safeher.presentation.emergency.EmergencyViewModel
import com.example.safeher.ui.theme.EmergencyRed
import com.example.safeher.utils.DemoManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onBack: () -> Unit,
    viewModel: EmergencyViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDemoMode by DemoManager.isDemoMode.collectAsState()

    val sosState by viewModel.sosState.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val realHistory by viewModel.emergencyHistory.collectAsState()
    val demoHistory by DemoManager.demoAlerts.collectAsState()
    val history = if (isDemoMode) demoHistory else realHistory
    val sirenActive by viewModel.sirenActive.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (fineLocation) {
            viewModel.startCountdown(context)
        } else {
            viewModel.triggerSOS(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isDemoMode) "Emergency SOS (Demo)" else "Emergency SOS & Siren") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.cancelSOS(context)
                        onBack()
                    }) {
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
                .background(
                    if (sosState is EmergencyViewModel.SOSState.Active) EmergencyRed.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.background
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (sosState) {
                is EmergencyViewModel.SOSState.Idle -> {
                    Text(
                        text = if (isDemoMode) "DEMO EMERGENCY ALERT" else "EMERGENCY ALERT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmergencyRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isDemoMode)
                            "Demo Mode: Triggering SOS will simulate location fetching and alert logging without sending real SMS."
                        else
                            "Tap below to immediately trigger SOS, sound siren, fetch GPS location, and alert preferred contacts.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SafeHerButton(
                        text = if (isDemoMode) "TRIGGER SIMULATED SOS" else "TRIGGER SOS",
                        onClick = {
                            if (isDemoMode) {
                                viewModel.startCountdown(context)
                                val simAlert = Alert(
                                    userId = DemoManager.demoUser.uid,
                                    latitude = 31.5204,
                                    longitude = 74.3587,
                                    timestamp = System.currentTimeMillis(),
                                    status = "Simulated SOS Triggered"
                                )
                                DemoManager.addDemoAlert(simAlert)
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    viewModel.startCountdown(context)
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        },
                        isDestructive = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.toggleSiren(context, !sirenActive) },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(if (sirenActive) "Stop Emergency Siren" else "Sound Emergency Siren Only")
                    }
                }
                is EmergencyViewModel.SOSState.Countdown -> {
                    Text(
                        text = "SOS in $countdown seconds...",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmergencyRed
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SafeHerButton(
                        text = "CANCEL ACCIDENTAL SOS",
                        onClick = { viewModel.cancelCountdown(context) }
                    )
                }
                is EmergencyViewModel.SOSState.Active -> {
                    Text(
                        text = if (isDemoMode) "SIMULATED SOS ACTIVE!" else "SOS ALERT ACTIVE!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmergencyRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(if (isDemoMode) "Simulated location logged. No real SMS sent." else "Siren sounding, location fetched & contacts alerted.")
                    Spacer(modifier = Modifier.height(24.dp))
                    SafeHerButton(
                        text = "End Emergency",
                        onClick = { viewModel.cancelSOS(context) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isDemoMode) "Emergency History (Demo Data)" else "Emergency History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No past emergency alerts.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { alert ->
                        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(alert.timestamp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Alert Status: ${alert.status}", fontWeight = FontWeight.Bold, color = EmergencyRed)
                                Text(text = "Time: $dateStr", fontSize = 12.sp)
                                Text(text = "Lat: ${alert.latitude}, Lng: ${alert.longitude}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

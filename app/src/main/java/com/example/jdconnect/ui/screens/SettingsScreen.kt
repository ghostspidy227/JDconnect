package com.example.jdconnect.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jdconnect.data.repository.SettingsRepository
import com.example.jdconnect.fcm.FcmTokenProvider
import com.example.jdconnect.model.AppSettings

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context.applicationContext) }
    var showCopiedSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load current settings once
    var appSettings by remember { mutableStateOf(settingsRepository.getSettings()) }

    LaunchedEffect(showCopiedSnackbar) {
        if (showCopiedSnackbar) {
            snackbarHostState.showSnackbar("FCM token copied to clipboard")
            showCopiedSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // FCM Token Section
            Text(
                text = "FCM Token",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Copy this token to configure your server to send notifications to this device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val token = FcmTokenProvider.getToken()
            if (token != null) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("FCM Token", token)
                        clipboard.setPrimaryClip(clip)
                        showCopiedSnackbar = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copy FCM Token")
                }
            } else {
                Text(
                    text = "Token not available yet. Please restart the app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Heartbeat / offline threshold settings
            Text(
                text = "Offline detection",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Expected heartbeat interval (minutes)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = appSettings.defaultHeartbeatMinutes.toString(),
                onValueChange = { value ->
                    val minutes = value.toIntOrNull()
                    if (minutes != null && minutes > 0) {
                        appSettings = appSettings.copy(defaultHeartbeatMinutes = minutes)
                        settingsRepository.updateSettings(appSettings)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Grace multiplier (1.0–5.0)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = String.format("%.1f", appSettings.graceMultiplier),
                onValueChange = { value ->
                    val grace = value.toDoubleOrNull()
                    if (grace != null && grace in 1.0..5.0) {
                        appSettings = appSettings.copy(graceMultiplier = grace)
                        settingsRepository.updateSettings(appSettings)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

package com.example.jdconnect

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jdconnect.connection.ConnectionStateEvaluator
import com.example.jdconnect.ui.AppRoot
import com.example.jdconnect.ui.theme.JDconnectTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JDconnectTheme {
                AppRoot()
            }
        }

        ensureNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        // When the app comes to the foreground, re-evaluate connection state
        // based on lastHeartbeatAt and mark stale servers as OFFLINE.
        ConnectionStateEvaluator.evaluateNow(applicationContext)
    }

    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}

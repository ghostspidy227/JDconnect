package com.example.jdconnect.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jdconnect.fcm.FcmTokenProvider

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "Copy FCM token",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .clickable {
                    copyToken(context)
                }
        )

        Text(
            text = "Tap to copy device token for server registration",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun copyToken(context: Context) {
    val token = FcmTokenProvider.getToken()

    if (token == null) {
        Toast.makeText(context, "Token not available yet", Toast.LENGTH_SHORT).show()
        return
    }

    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    clipboard.setPrimaryClip(
        ClipData.newPlainText("FCM Token", token)
    )

    Toast.makeText(context, "FCM token copied", Toast.LENGTH_SHORT).show()
}

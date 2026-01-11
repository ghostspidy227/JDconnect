package com.example.jdconnect.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.jdconnect.fcm.ingest.MessageIngestor
import org.json.JSONObject

class JDConnectMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("JDconnect-FCM", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.data.isEmpty()) {
            Log.d("JDconnect-FCM", "Empty data message ignored")
            return
        }

        val rawJson = JSONObject(message.data as Map<*, *>).toString()
        Log.d("JDconnect-FCM", "Raw message: $rawJson")

        try {
            MessageIngestor.ingest(applicationContext, rawJson)
        } catch (e: Exception) {
            Log.e("JDconnect-FCM", "Ingest failed", e)
        }
    }
}

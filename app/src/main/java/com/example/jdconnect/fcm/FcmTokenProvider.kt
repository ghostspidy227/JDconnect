package com.example.jdconnect.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FcmTokenProvider {

    @Volatile
    private var token: String? = null

    fun init() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token = it
            Log.d("JDconnect-FCM", "FCM token updated")
        }
    }

    fun getToken(): String? = token
}

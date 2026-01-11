package com.example.jdconnect

import android.app.Application
import com.example.jdconnect.fcm.FcmTokenProvider
import com.example.jdconnect.notifications.NotificationChannels

class JDconnectApp : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationChannels.createAll(this)
        FcmTokenProvider.init()
    }
}

package com.example.jdconnect.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build

object NotificationChannels {

    const val RESOURCE = "resource_events"
    const val SERVICE = "service_events"
    const val INVALID = "invalid_events"
    const val HEARTBEAT = "heartbeat_events"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channels = listOf(
            NotificationChannel(
                RESOURCE,
                "Resource events",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "CPU, memory, disk events"
            },

            NotificationChannel(
                SERVICE,
                "Service events",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Service failures and restarts"
            },

            NotificationChannel(
                INVALID,
                "Invalid messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Protocol or payload errors"
                enableLights(true)
                lightColor = Color.RED
            },

            NotificationChannel(
                HEARTBEAT,
                "Heartbeat",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Connectivity heartbeats"
            }
        )

        manager.createNotificationChannels(channels)
    }
}

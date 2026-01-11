package com.example.jdconnect.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.jdconnect.R
import com.example.jdconnect.model.Event
import com.example.jdconnect.model.EventCategory
import com.example.jdconnect.model.EventLevel
import kotlin.random.Random

object NotificationDispatcher {

    fun maybeNotify(context: Context, event: Event) {

        // Categories that NEVER notify (by design)
        when (event.category) {
            EventCategory.HEARTBEAT -> return
            else -> {}
        }

        val channel = when (event.category) {
            EventCategory.RESOURCE -> NotificationChannels.RESOURCE
            EventCategory.SERVICE -> NotificationChannels.SERVICE
            EventCategory.INVALID -> NotificationChannels.INVALID

            // Connectivity events are state changes and should be visible.
            EventCategory.CONNECTIVITY -> NotificationChannels.SERVICE

            // These are informational / system-level
            EventCategory.SYSTEM,
            EventCategory.CUSTOM -> NotificationChannels.SERVICE

            // Heartbeats never notify, but keep a channel for potential diagnostics.
            EventCategory.HEARTBEAT -> NotificationChannels.HEARTBEAT
        }

        val icon = when (event.level) {
            EventLevel.CRITICAL -> R.drawable.ic_error
            EventLevel.WARNING -> R.drawable.ic_warning
            EventLevel.INFO,
            EventLevel.UNKNOWN -> R.drawable.ic_info
        }

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(icon)
            .setContentTitle(event.title)
            .setContentText(event.message ?: "No details")
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(Random.nextInt(), notification)
    }
}

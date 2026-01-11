package com.example.jdconnect.connection

import android.content.Context
import com.example.jdconnect.data.db.DatabaseProvider
import com.example.jdconnect.data.repository.EventRepository
import com.example.jdconnect.data.repository.ServerRepository
import com.example.jdconnect.data.repository.SettingsRepository
import com.example.jdconnect.model.Event
import com.example.jdconnect.model.EventCategory
import com.example.jdconnect.model.EventLevel
import com.example.jdconnect.model.EventType
import com.example.jdconnect.notifications.NotificationDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Central place for deriving ONLINE/OFFLINE from lastHeartbeatAt and emitting connectivity events.
 */
object ConnectionStateEvaluator {

    /**
     * Fire-and-forget entry point for app code that only has a Context.
     */
    fun evaluateNow(context: Context) {
        val db = DatabaseProvider.get(context)
        val serverRepo = ServerRepository(db.serverDao())
        val eventRepo = EventRepository(db.eventDao())

        CoroutineScope(Dispatchers.IO).launch {
            evaluateAndEmit(
                context = context,
                serverRepository = serverRepo,
                eventRepository = eventRepo
            )
        }
    }

    /**
     * Core logic used from both app lifecycle (MainActivity) and FCM ingest.
     */
    suspend fun evaluateAndEmit(
        context: Context,
        serverRepository: ServerRepository,
        eventRepository: EventRepository,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        val offlineThresholdMs = computeOfflineThresholdMs(context)

        val newlyOfflineServers = serverRepository.markOfflineServers(
            nowMillis = nowMillis,
            offlineThresholdMs = offlineThresholdMs
        )

        newlyOfflineServers.forEach { server ->
            val event = Event(
                version = 1,
                eventId = "connectivity-offline-${server.id}-$nowMillis",
                eventType = EventType.CONNECTIVITY,
                timestamp = nowMillis,
                receivedAt = nowMillis,
                nodeId = "unknown",
                serverId = server.id,
                category = EventCategory.CONNECTIVITY,
                level = EventLevel.INFO,
                title = "Connection lost",
                message = "Server is now offline"
            )

            eventRepository.insertEvent(event)
            NotificationDispatcher.maybeNotify(context, event)
        }
    }

    private fun computeOfflineThresholdMs(context: Context): Long {
        val settingsRepo = SettingsRepository(context.applicationContext)
        val settings = settingsRepo.getSettings()

        val minutes = settings.defaultHeartbeatMinutes.coerceAtLeast(1)
        val grace = settings.graceMultiplier.coerceIn(1.0, 5.0)

        return (minutes * grace * 60_000L).toLong()
    }
}

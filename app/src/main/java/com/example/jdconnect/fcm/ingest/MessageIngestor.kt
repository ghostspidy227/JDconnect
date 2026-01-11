package com.example.jdconnect.fcm.ingest

import android.content.Context
import com.example.jdconnect.data.db.DatabaseProvider
import com.example.jdconnect.data.repository.EventRepository
import com.example.jdconnect.data.repository.ServerRepository
import com.example.jdconnect.fcm.parser.MessageParser
import com.example.jdconnect.fcm.parser.ParsedMessage
import com.example.jdconnect.model.Event
import com.example.jdconnect.model.EventCategory
import com.example.jdconnect.model.EventLevel
import com.example.jdconnect.model.EventType
import com.example.jdconnect.notifications.NotificationDispatcher
import com.example.jdconnect.connection.ConnectionStateEvaluator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object MessageIngestor {

    fun ingest(context: Context, rawJson: String) {
        val db = DatabaseProvider.get(context)
        val serverRepo = ServerRepository(db.serverDao())
        val eventRepo = EventRepository(db.eventDao())

        CoroutineScope(Dispatchers.IO).launch {
            // On every FCM wake, re-evaluate offline servers to derive OFFLINE state.
            ConnectionStateEvaluator.evaluateAndEmit(
                context = context,
                serverRepository = serverRepo,
                eventRepository = eventRepo
            )

            when (val parsed = MessageParser.parse(rawJson)) {

                is ParsedMessage.ServerMsg -> {
                    serverRepo.insertServers(listOf(parsed.server))
                }

                is ParsedMessage.EventMsg -> {
                    eventRepo.insertEvent(parsed.event)
                    NotificationDispatcher.maybeNotify(context, parsed.event)
                }

                is ParsedMessage.HeartbeatMsg -> {
                    // Update heartbeat timestamp and check for state transition
                    val transitionedToOnline = serverRepo.processHeartbeat(
                        serverId = parsed.serverId,
                        timestamp = parsed.ts
                    )

                    // If server transitioned from OFFLINE to ONLINE, emit CONNECTIVITY event
                    if (transitionedToOnline) {
                        val connectivityEvent = Event(
                            version = 1,
                            eventId = "connectivity-${parsed.serverId}-${System.currentTimeMillis()}",
                            eventType = EventType.CONNECTIVITY,
                            timestamp = parsed.ts,
                            receivedAt = System.currentTimeMillis(),
                            nodeId = parsed.nodeId,
                            serverId = parsed.serverId,
                            category = EventCategory.CONNECTIVITY,
                            level = EventLevel.INFO,
                            title = "Connection restored",
                            message = "Server is now online"
                        )

                        eventRepo.insertEvent(connectivityEvent)
                        NotificationDispatcher.maybeNotify(context, connectivityEvent)
                    }
                }

                is ParsedMessage.InvalidMsg -> {
                    val serverId = try {
                        val root = JSONObject(parsed.raw)
                        val type = root.optString("type")

                        if (type == "event") {
                            val eventRaw = root.optString("event", null)
                            if (eventRaw != null) {
                                JSONObject(eventRaw).optString("server_id", null)
                            } else null
                        } else null
                    } catch (e: Exception) {
                        null
                    }

                    val invalidEvent = Event(
                        version = 1,
                        eventId = "invalid-${System.currentTimeMillis()}",
                        eventType = EventType.EVENT,
                        timestamp = System.currentTimeMillis(),
                        receivedAt = System.currentTimeMillis(),
                        nodeId = "unknown",
                        serverId = serverId,
                        category = EventCategory.INVALID,
                        level = EventLevel.UNKNOWN,
                        title = "Invalid message received",
                        message = parsed.reason,
                        rawPayload = parsed.raw
                    )

                    eventRepo.insertEvent(invalidEvent)
                    NotificationDispatcher.maybeNotify(context, invalidEvent)
                }
            }
        }
    }
}

package com.example.jdconnect.data.seed

import com.example.jdconnect.data.repository.EventRepository
import com.example.jdconnect.data.repository.ServerRepository
import com.example.jdconnect.model.*

/**
 * DEVELOPMENT / DEMO ONLY
 *
 * This seeder is not used by the app and is intended solely for
 * manual testing or UI previews.
 */


object DatabaseSeeder {

    suspend fun seed(
        serverRepo: ServerRepository,
        eventRepo: EventRepository
    ) {
        val servers = listOf(
            Server(
                id = "arch-server",
                friendlyName = "Arch Server",
                hostname = "arch.local",
                vpnAddress = "100.100.234.213"
            ),
            Server(
                id = "home-pi",
                friendlyName = "Home Pi",
                hostname = "pi.local",
                vpnAddress = "100.100.234.214"
            )
        )

        serverRepo.insertServers(servers)

        val now = System.currentTimeMillis()

        val events = listOf(
            Event(
                version = 1,
                eventId = "seed-1",
                eventType = EventType.EVENT,
                timestamp = now - 60_000,
                receivedAt = now,
                nodeId = "home-node",
                serverId = "arch-server",
                category = EventCategory.RESOURCE,
                level = EventLevel.INFO,
                title = "Server idle",
                message = "No activity detected for 45 minutes"
            ),
            Event(
                version = 1,
                eventId = "seed-2",
                eventType = EventType.HEARTBEAT,
                timestamp = now,
                receivedAt = now,
                nodeId = "home-node",
                serverId = "home-pi",
                category = EventCategory.CONNECTIVITY,
                level = EventLevel.INFO,
                title = "Heartbeat received",
                message = null
            )
        )

        events.forEach { eventRepo.insertEvent(it) }
    }
}

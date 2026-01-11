package com.example.jdconnect.data.repository

import com.example.jdconnect.data.dao.EventDao
import com.example.jdconnect.data.entity.EventEntity
import com.example.jdconnect.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(
    private val dao: EventDao
) {

    fun getAllEvents(): Flow<List<Event>> {
        return dao.getAllEvents().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getEventsForServer(serverId: String): Flow<List<Event>> {
        return dao.getEventsForServer(serverId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun insertEvent(event: Event) {
        dao.insertEvent(event.toEntity())
    }

    suspend fun purgeOldEvents(cutoff: Long) {
        dao.purgeOldEvents(cutoff)
    }

    private fun EventEntity.toModel(): Event =
        Event(
            version = version,
            eventId = eventId,
            eventType = EventType.valueOf(eventType),
            timestamp = timestamp,
            receivedAt = receivedAt,
            nodeId = nodeId,
            serverId = serverId,
            category = EventCategory.valueOf(category),
            level = EventLevel.valueOf(level),
            title = title,
            message = message,
            tags = tags?.split(",") ?: emptyList(),
            metrics = null, // metrics decoding later
            rawPayload = rawPayload,
            ttlSeconds = ttlSeconds
        )

    private fun Event.toEntity(): EventEntity =
        EventEntity(
            eventId = eventId,
            version = version,
            eventType = eventType.name,
            timestamp = timestamp,
            receivedAt = receivedAt,
            nodeId = nodeId,
            serverId = serverId,
            category = category.name,
            level = level.name,
            title = title,
            message = message,
            tags = tags.joinToString(","),
            metricsJson = null,
            rawPayload = rawPayload,
            ttlSeconds = ttlSeconds
        )
}

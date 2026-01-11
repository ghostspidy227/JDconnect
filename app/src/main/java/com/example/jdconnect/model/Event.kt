package com.example.jdconnect.model

data class Event(
    val version: Int,

    val eventId: String,
    val eventType: EventType,

    val timestamp: Long,             // event time (server-side)
    val receivedAt: Long,            // arrival time (device-side)

    val nodeId: String,              // always-on node ID
    val serverId: String?,            // nullable for node-wide events

    val category: EventCategory,
    val level: EventLevel,

    val title: String,
    val message: String?,

    val tags: List<String> = emptyList(),

    val metrics: EventMetrics? = null,

    val rawPayload: String? = null,  // used ONLY for invalid events

    val ttlSeconds: Long = 604800     // default: 7 days
)

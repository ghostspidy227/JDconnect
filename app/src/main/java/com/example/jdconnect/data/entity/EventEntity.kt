package com.example.jdconnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val eventId: String,

    val version: Int,
    val eventType: String,

    val timestamp: Long,
    val receivedAt: Long,

    val nodeId: String,
    val serverId: String?,

    val category: String,
    val level: String,

    val title: String,
    val message: String?,

    val tags: String?,          // stored as CSV
    val metricsJson: String?,   // stored as JSON

    val rawPayload: String?,
    val ttlSeconds: Long
)

package com.example.jdconnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "node_state")
data class NodeStateEntity(
    @PrimaryKey
    val nodeId: String,

    val lastHeartbeatAt: Long?,
    val connectionState: String,

    val expectedHeartbeatMinutes: Int,
    val graceMultiplier: Double
)

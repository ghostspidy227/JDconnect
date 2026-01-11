package com.example.jdconnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey
    val id: String,

    val friendlyName: String,
    val hostname: String?,
    val vpnAddress: String?,

    val lastHeartbeatAt: Long?,
    val isOnline: Boolean,

    // Per-server notification control
    val notificationsEnabled: Boolean
)

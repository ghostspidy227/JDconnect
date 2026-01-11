package com.example.jdconnect.model

/**
 * Server domain model (not a Room entity).
 * Use ServerEntity for database operations.
 */
data class Server(
    val id: String,
    val friendlyName: String,
    val hostname: String? = null,
    val vpnAddress: String? = null,

    val lastHeartbeatAt: Long? = null,
    val isOnline: Boolean = false,

    // Per-server notification control
    val notificationsEnabled: Boolean = true
)

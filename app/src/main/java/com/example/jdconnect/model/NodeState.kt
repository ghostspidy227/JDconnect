package com.example.jdconnect.model

data class NodeState(
    val nodeId: String,
    val lastHeartbeatAt: Long?,
    val connectionState: ConnectionState,
    val expectedHeartbeatMinutes: Int,
    val graceMultiplier: Double
)

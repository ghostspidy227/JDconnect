package com.example.jdconnect.model

data class AppSettings(
    val heartbeatMonitoringEnabled: Boolean = true,
    val defaultHeartbeatMinutes: Int = 30,
    val graceMultiplier: Double = 1.5
)

package com.example.jdconnect.model

enum class EventCategory {
    SYSTEM,
    SERVICE,
    RESOURCE,
    CUSTOM,
    CONNECTIVITY,

    // Used when category is missing or unrecognized
    INVALID,
    HEARTBEAT
}

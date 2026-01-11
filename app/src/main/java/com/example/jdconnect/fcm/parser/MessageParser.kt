package com.example.jdconnect.fcm.parser

import com.example.jdconnect.model.*
import org.json.JSONObject

sealed class ParsedMessage {
    data class ServerMsg(val server: Server) : ParsedMessage()
    data class EventMsg(val event: Event) : ParsedMessage()
    data class HeartbeatMsg(val serverId: String, val nodeId: String, val ts: Long) : ParsedMessage()
    data class InvalidMsg(val raw: String, val reason: String) : ParsedMessage()
}

object MessageParser {

    fun parse(rawJson: String): ParsedMessage {
        return try {
            val root = JSONObject(rawJson)

            val version = root.getInt("v")
            if (version != 1) {
                return ParsedMessage.InvalidMsg(rawJson, "Unsupported version")
            }

            val type = root.getString("type")
            val nodeId = root.getString("node_id")
            val ts = root.getLong("ts")

            when (type) {
                "server" -> parseServer(root)
                "event" -> parseEvent(root, nodeId, ts)
                "heartbeat" -> parseHeartbeat(root, nodeId, ts)
                else -> ParsedMessage.InvalidMsg(rawJson, "Unknown type")
            }
        } catch (e: Exception) {
            ParsedMessage.InvalidMsg(rawJson, "Exception: ${e.message}")
        }
    }

    private fun parseServer(root: JSONObject): ParsedMessage {
        val serverRaw = root.getString("server")
        val s = JSONObject(serverRaw)

        val server = Server(
            id = s.getString("id"),
            friendlyName = s.getString("name"),
            hostname = s.optString("hostname", null),
            vpnAddress = s.optString("vpn_ip", null)
        )

        return ParsedMessage.ServerMsg(server)
    }


    private fun parseEvent(root: JSONObject, nodeId: String, ts: Long): ParsedMessage {
        val eventRaw = root.getString("event")
        val e = JSONObject(eventRaw)

        val event = Event(
            version = root.getInt("v"),
            eventId = e.getString("id"),
            eventType = EventType.EVENT,
            timestamp = ts,
            receivedAt = System.currentTimeMillis(),
            nodeId = nodeId,
            serverId = e.getString("server_id"),
            category = EventCategory.valueOf(e.getString("category")),
            level = EventLevel.valueOf(e.getString("level")),
            title = e.getString("title"),
            message = e.optString("message", null),
            tags = emptyList()
        )

        return ParsedMessage.EventMsg(event)
    }


    private fun parseHeartbeat(root: JSONObject, nodeId: String, ts: Long): ParsedMessage {
        // Flat structure: server_id is at root level
        val serverId = root.getString("server_id")
        return ParsedMessage.HeartbeatMsg(
            serverId = serverId,
            nodeId = nodeId,
            ts = ts
        )
    }
}

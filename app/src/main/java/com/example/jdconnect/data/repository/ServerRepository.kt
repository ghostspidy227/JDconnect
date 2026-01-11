package com.example.jdconnect.data.repository

import com.example.jdconnect.data.dao.ServerDao
import com.example.jdconnect.data.entity.ServerEntity
import com.example.jdconnect.model.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServerRepository(
    private val dao: ServerDao
) {

    fun getServers(): Flow<List<Server>> {
        return dao.getAllServers().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getServerById(serverId: String): Server? {
        return dao.getServerById(serverId)?.toModel()
    }

    suspend fun insertServers(servers: List<Server>) {
        dao.insertServers(servers.map { it.toEntity() })
    }

    suspend fun deleteServer(server: Server) {
        dao.deleteServer(server.toEntity())
    }

    /**
     * Process heartbeat: update timestamp and handle state transitions.
     * Returns true if server transitioned from OFFLINE to ONLINE.
     */
    suspend fun processHeartbeat(serverId: String, timestamp: Long): Boolean {
        val server = dao.getServerById(serverId)
        val wasOffline = server?.isOnline == false

        // Update heartbeat timestamp and mark online
        dao.updateHeartbeat(serverId, timestamp, isOnline = true)

        // Return true if this is a state transition (OFFLINE -> ONLINE)
        return wasOffline
    }

    /**
     * Evaluate all servers and mark them OFFLINE if their lastHeartbeatAt is older than the threshold.
     * Returns the list of servers that transitioned from ONLINE to OFFLINE.
     */
    suspend fun markOfflineServers(nowMillis: Long, offlineThresholdMs: Long): List<Server> {
        val entities = dao.getAllServersOnce()
        val newlyOffline = mutableListOf<Server>()

        for (entity in entities) {
            val last = entity.lastHeartbeatAt ?: continue
            if (entity.isOnline && nowMillis - last > offlineThresholdMs) {
                // Mark as offline while preserving the last heartbeat timestamp.
                dao.updateHeartbeat(entity.id, last, isOnline = false)
                newlyOffline += entity.toModel().copy(isOnline = false)
            }
        }

        return newlyOffline
    }

    private fun ServerEntity.toModel(): Server =
        Server(
            id = id,
            friendlyName = friendlyName,
            hostname = hostname,
            vpnAddress = vpnAddress,
            lastHeartbeatAt = lastHeartbeatAt,
            isOnline = isOnline,
            notificationsEnabled = notificationsEnabled
        )

    private fun Server.toEntity(): ServerEntity =
        ServerEntity(
            id = id,
            friendlyName = friendlyName,
            hostname = hostname,
            vpnAddress = vpnAddress,
            lastHeartbeatAt = lastHeartbeatAt,
            isOnline = isOnline,
            notificationsEnabled = notificationsEnabled
        )
}

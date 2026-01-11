package com.example.jdconnect.data.dao

import androidx.room.*
import com.example.jdconnect.data.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

    @Query("SELECT * FROM servers")
    fun getAllServers(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers")
    suspend fun getAllServersOnce(): List<ServerEntity>

    @Query("SELECT * FROM servers WHERE id = :serverId")
    suspend fun getServerById(serverId: String): ServerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    @Query("UPDATE servers SET lastHeartbeatAt = :timestamp, isOnline = :isOnline WHERE id = :serverId")
    suspend fun updateHeartbeat(serverId: String, timestamp: Long, isOnline: Boolean)

    @Delete
    suspend fun deleteServer(server: ServerEntity)
}

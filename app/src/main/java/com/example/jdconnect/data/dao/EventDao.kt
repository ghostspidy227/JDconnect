package com.example.jdconnect.data.dao

import androidx.room.*
import com.example.jdconnect.data.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY receivedAt DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE serverId = :serverId ORDER BY receivedAt DESC")
    fun getEventsForServer(serverId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE receivedAt < :cutoff")
    suspend fun purgeOldEvents(cutoff: Long)
}

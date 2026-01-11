package com.example.jdconnect.data.dao

import androidx.room.*
import com.example.jdconnect.data.entity.NodeStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeStateDao {

    @Query("SELECT * FROM node_state WHERE nodeId = :nodeId")
    fun getNodeState(nodeId: String): Flow<NodeStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: NodeStateEntity)
}

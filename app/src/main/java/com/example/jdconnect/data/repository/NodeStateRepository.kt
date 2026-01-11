package com.example.jdconnect.data.repository

import com.example.jdconnect.data.dao.NodeStateDao
import com.example.jdconnect.data.entity.NodeStateEntity
import com.example.jdconnect.model.ConnectionState
import com.example.jdconnect.model.NodeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NodeStateRepository(
    private val dao: NodeStateDao
) {

    fun getNodeState(nodeId: String): Flow<NodeState?> {
        return dao.getNodeState(nodeId).map { it?.toModel() }
    }

    suspend fun upsert(state: NodeState) {
        dao.upsert(state.toEntity())
    }

    private fun NodeStateEntity.toModel(): NodeState =
        NodeState(
            nodeId = nodeId,
            lastHeartbeatAt = lastHeartbeatAt,
            connectionState = ConnectionState.valueOf(connectionState),
            expectedHeartbeatMinutes = expectedHeartbeatMinutes,
            graceMultiplier = graceMultiplier
        )

    private fun NodeState.toEntity(): NodeStateEntity =
        NodeStateEntity(
            nodeId = nodeId,
            lastHeartbeatAt = lastHeartbeatAt,
            connectionState = connectionState.name,
            expectedHeartbeatMinutes = expectedHeartbeatMinutes,
            graceMultiplier = graceMultiplier
        )
}

package com.example.jdconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jdconnect.data.repository.ServerRepository
import com.example.jdconnect.model.Server
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServerViewModel(
    private val repository: ServerRepository
) : ViewModel() {

    private val _servers = MutableStateFlow<List<Server>>(emptyList())
    val servers: StateFlow<List<Server>> = _servers

    fun start() {
        viewModelScope.launch {
            repository.getServers().collectLatest {
                _servers.value = it
            }
        }
    }

    fun deleteServer(server: Server) {
        viewModelScope.launch {
            repository.deleteServer(server)
        }
    }
}

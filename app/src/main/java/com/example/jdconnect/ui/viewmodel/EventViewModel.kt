package com.example.jdconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jdconnect.data.repository.EventRepository
import com.example.jdconnect.model.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private var currentJob: Job? = null

    fun loadForServer(serverId: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            repository.getEventsForServer(serverId).collectLatest {
                _events.value = it
            }
        }
    }
}

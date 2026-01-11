package com.example.jdconnect.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.jdconnect.data.db.DatabaseProvider
import com.example.jdconnect.data.repository.EventRepository
import com.example.jdconnect.data.repository.ServerRepository
import com.example.jdconnect.model.Server
import com.example.jdconnect.ui.screens.*
import com.example.jdconnect.ui.viewmodel.EventViewModel
import com.example.jdconnect.ui.viewmodel.ServerViewModel

enum class Screen {
    SERVERS,
    EVENTS,
    SETTINGS
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.get(context) }

    val serverRepo = remember { ServerRepository(db.serverDao()) }
    val eventRepo = remember { EventRepository(db.eventDao()) }

    val serverViewModel = remember {
        ServerViewModel(serverRepo).apply { start() }
    }

    val eventViewModel = remember {
        EventViewModel(eventRepo)
    }

    val servers by serverViewModel.servers.collectAsState()
    val events by eventViewModel.events.collectAsState()

    var selectedServer by remember { mutableStateOf<Server?>(null) }
    var currentScreen by remember { mutableStateOf(Screen.SERVERS) }

    when (currentScreen) {
        Screen.SERVERS ->
            ServerListScreen(
                servers = servers,
                onServerSelected = { server ->
                    selectedServer = server
                    eventViewModel.loadForServer(server.id)
                    currentScreen = Screen.EVENTS
                },
                onDeleteServer = { server ->
                    serverViewModel.deleteServer(server)
                },
                onOpenSettings = {
                    currentScreen = Screen.SETTINGS
                }
            )

        Screen.EVENTS ->
            EventFeedScreen(
                server = selectedServer,
                events = events,
                onBack = {
                    currentScreen = Screen.SERVERS
                }
            )

        Screen.SETTINGS ->
            SettingsScreen(
                onBack = {
                    currentScreen = Screen.SERVERS
                }
            )
    }
}

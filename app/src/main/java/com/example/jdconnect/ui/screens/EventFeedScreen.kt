package com.example.jdconnect.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jdconnect.model.Event
import com.example.jdconnect.model.Server
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFeedScreen(
    server: Server?,
    events: List<Event>,
    onBack: () -> Unit
) {
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    if (selectedEvent != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedEvent = null }
        ) {
            EventDetailContent(event = selectedEvent!!, server = server) {
                selectedEvent = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Text(
            text = server?.friendlyName ?: "Unknown server",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (events.isEmpty()) {
            Text(
                text = "No events yet.\nWaiting for server activity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            events.forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedEvent = event }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )

                        event.message?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventDetailContent(
    event: Event,
    server: Server?,
    onClose: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Event details",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onClose) {
                Text("Close")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = event.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        event.message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metadata section
        Text(
            text = "Metadata",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Server: ${server?.friendlyName ?: event.serverId ?: "Unknown"}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Category: ${event.category}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Level: ${event.level}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Node: ${event.nodeId}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Event ID: ${event.eventId}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "At: ${dateFormat.format(Date(event.timestamp))}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Raw payload only for INVALID events (for debugging)
        if (event.rawPayload != null && event.category.name == "INVALID") {
            Text(
                text = "Raw payload",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.rawPayload,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

package com.eventos.pf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventos.pf.model.Event
import com.eventos.pf.ui.components.EventCard
import com.eventos.pf.ui.components.InfoBanner
import com.eventos.pf.ui.components.StatePlaceholder
import com.eventos.pf.ui.components.TopBar

@Composable
fun EventListScreen(
    events: List<Event>,
    onSelect: (Event) -> Unit,
    onHistory: () -> Unit,
    errorMessage: String? = null,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        TopBar(title = "Eventos", trailing = {
            TextButton(onClick = onHistory) {
                Text("Mis compras")
            }
        })
        InfoBanner(text = "Explorá los eventos disponibles.")
        Spacer(modifier = Modifier.height(12.dp))
        when {
            errorMessage != null -> {
                StatePlaceholder(
                    title = "No pudimos cargar los eventos",
                    description = errorMessage,
                    actionLabel = onRetry?.let { "Reintentar" },
                    onAction = onRetry
                )
            }
            events.isEmpty() -> {
                StatePlaceholder(
                    title = "No hay eventos disponibles",
                    description = "Cuando haya nuevos eventos los verás acá.",
                    actionLabel = onRetry?.let { "Actualizar" },
                    onAction = onRetry
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        EventCard(
                            title = event.title,
                            subtitle = event.summary,
                            chips = listOf(event.dateTime, event.type, event.priceLabel),
                            supporting = "${event.seatsAvailable}/${event.seatsTotal} asientos libres"
                        ) { onSelect(event) }
                    }
                }
            }
        }
    }
}

package com.eventos.pf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventos.pf.model.Purchase
import com.eventos.pf.ui.components.StatePlaceholder
import com.eventos.pf.ui.components.TopBar

@Composable
fun HistoryScreen(
    history: List<Purchase>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        TopBar(title = "Mis compras", onBack = onBack)
        if (history.isEmpty()) {
            StatePlaceholder(
                title = "Todavía no registrás compras",
                description = "Cuando confirmes una compra aparecerá aquí.",
                actionLabel = "Volver al inicio",
                onAction = onBack,
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { item ->
                    PurchaseCard(item)
                }
            }
        }
    }
}

@Composable
private fun PurchaseCard(purchase: Purchase) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(purchase.event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(purchase.event.dateTime, style = MaterialTheme.typography.bodyMedium)
        Text("Asientos: ${purchase.seats.joinToString { it.label }}", style = MaterialTheme.typography.bodySmall)
        Text("Total: ${purchase.totalLabel}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text("Estado: ${purchase.status}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


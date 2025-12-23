package com.eventos.pf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventos.pf.model.AttendeeInput
import com.eventos.pf.model.Event
import com.eventos.pf.ui.components.PrimaryButton
import com.eventos.pf.ui.components.SecondaryButton
import com.eventos.pf.ui.components.SectionCard
import com.eventos.pf.ui.components.TopBar

@Composable
fun ConfirmationScreen(
    event: Event,
    seats: List<String>,
    attendees: List<AttendeeInput>,
    totalLabel: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopBar(title = "Confirmación", onBack = onBack)
        SectionCard(title = event.title, supporting = event.dateTime) {
            Text(event.address, style = MaterialTheme.typography.bodyMedium)
            Text(event.priceLabel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("${seats.size} asientos seleccionados", style = MaterialTheme.typography.bodySmall)
        }
        SectionCard(title = "Asientos") {
            Text(seats.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
        }
        SectionCard(title = "Asistentes") {
            attendees.forEach {
                Text("• ${it.firstName} ${it.lastName} (${it.seat.label})", style = MaterialTheme.typography.bodyMedium)
            }
        }
        SectionCard(title = "Total a pagar") {
            Text(totalLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        PrimaryButton(text = "Confirmar compra", enabled = seats.isNotEmpty(), onClick = onConfirm)
        SecondaryButton(text = "Volver", onClick = onBack)
    }
}


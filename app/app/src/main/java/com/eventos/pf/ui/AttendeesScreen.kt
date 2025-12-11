package com.eventos.pf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventos.pf.model.AttendeeInput
import com.eventos.pf.ui.components.LabeledField
import com.eventos.pf.ui.components.PrimaryButton
import com.eventos.pf.ui.components.SecondaryButton
import com.eventos.pf.ui.components.TopBar

@Composable
fun AttendeesScreen(
    attendees: List<AttendeeInput>,
    onBack: () -> Unit,
    onChange: (AttendeeInput, String, String) -> Unit,
    onContinue: () -> Unit
) {
    val canContinue = attendees.isNotEmpty() && attendees.all { it.isComplete }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopBar(title = "Datos de asistentes", onBack = onBack)
        attendees.forEach { attendee ->
            AttendeeCard(attendee = attendee, onChange = onChange)
        }
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(text = "Continuar", enabled = canContinue, onClick = onContinue)
        SecondaryButton(text = "Volver al mapa", onClick = onBack)
    }
}

@Composable
private fun AttendeeCard(
    attendee: AttendeeInput,
    onChange: (AttendeeInput, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Asiento ${attendee.seat.label}",
            style = MaterialTheme.typography.titleSmall
        )
        LabeledField(
            label = "Nombre",
            value = attendee.firstName,
            onValueChange = { onChange(attendee, it, attendee.lastName) },
            placeholder = "Nombre del asistente"
        )
        LabeledField(
            label = "Apellido",
            value = attendee.lastName,
            onValueChange = { onChange(attendee, attendee.firstName, it) },
            placeholder = "Apellido del asistente"
        )
    }
}


package com.eventos.pf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.eventos.pf.model.PRICE_PER_SEAT
import com.eventos.pf.model.Seat
import com.eventos.pf.model.SeatStatus
import com.eventos.pf.ui.components.PrimaryButton
import com.eventos.pf.ui.components.SeatCell
import com.eventos.pf.ui.components.SeatLegend
import com.eventos.pf.ui.components.SeatChip
import com.eventos.pf.ui.components.TopBar

@Composable
fun SeatSelectionScreen(
    seats: List<Seat>,
    selectedSeats: List<Seat>,
    onBack: () -> Unit,
    onToggle: (Seat) -> Unit,
    onContinue: () -> Unit
) {
    val canContinue = selectedSeats.isNotEmpty()
    val selectedPositions = selectedSeats.map { it.row to it.column }.toSet()
    val displayedStatuses = seats.map { seat ->
        if (selectedPositions.contains(seat.row to seat.column)) SeatStatus.MINE else seat.status
    }
    val freeCount = displayedStatuses.count { it == SeatStatus.FREE }
    val soldCount = displayedStatuses.count { it == SeatStatus.SOLD }
    val heldCount = displayedStatuses.count { it == SeatStatus.HELD }
    val totalPrice = selectedSeats.size * PRICE_PER_SEAT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopBar(title = "Mapa de asientos", onBack = onBack)
            Text(
                text = "Seleccioná hasta 4 asientos. Los bloqueos expiran en 5 minutos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            SeatLegend()
            Spacer(modifier = Modifier.height(12.dp))
            SeatMetaInfo(
                freeCount = freeCount,
                soldCount = soldCount,
                heldCount = heldCount,
                totalPrice = totalPrice,
                hasSelection = selectedSeats.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SelectionSummary(selectedSeats = selectedSeats)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(seats) { seat ->
                    val status = if (selectedPositions.contains(seat.row to seat.column)) SeatStatus.MINE else seat.status
                    SeatCell(
                        label = seat.label,
                        status = status,
                        onClick = { onToggle(seat) }
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Seleccionados: ${selectedSeats.size} • Total estimado: $${totalPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            PrimaryButton(text = "Continuar", enabled = canContinue, onClick = onContinue)
        }
    }
}

@Composable
private fun SelectionSummary(selectedSeats: List<Seat>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Seleccionados: ${selectedSeats.size}/4",
                style = MaterialTheme.typography.titleMedium
            )
            if (selectedSeats.isNotEmpty()) {
                Text(
                    text = "Tocá un asiento para deseleccionar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (selectedSeats.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(selectedSeats) { seat ->
                    SeatChip(label = seat.label)
                }
            }
        } else {
            Text(
                text = "Elegí hasta 4 asientos para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeatMetaInfo(
    freeCount: Int,
    soldCount: Int,
    heldCount: Int,
    totalPrice: Int,
    hasSelection: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatChip(label = "Libres", value = freeCount)
            StatChip(label = "Vendidos", value = soldCount)
            StatChip(label = "Bloqueados", value = heldCount)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Precio por asiento: $${PRICE_PER_SEAT}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Total estimado: $${totalPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (!hasSelection) {
            Text(
                text = "Elegí hasta 4 asientos. Al continuar se bloquean por 5 minutos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatChip(label: String, value: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            value.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


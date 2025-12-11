package com.eventos.pf.model

import androidx.compose.runtime.Immutable

const val PRICE_PER_SEAT = 45_000

@Immutable
data class Session(
    val username: String,
    val rememberMe: Boolean
)

@Immutable
data class Presenter(
    val name: String,
    val role: String
)

@Immutable
data class Event(
    val id: String,
    val title: String,
    val summary: String,
    val description: String,
    val dateTime: String,
    val address: String,
    val priceLabel: String,
    val type: String,
    val presenters: List<Presenter>,
    val seatsAvailable: Int,
    val seatsTotal: Int,
    val coverUrl: String? = null
)

enum class SeatStatus { FREE, SOLD, MINE, HELD }

@Immutable
data class Seat(
    val row: Int,
    val column: Int,
    val status: SeatStatus
) {
    val label: String get() = "${row + 1}-${column + 1}"
}

@Immutable
data class AttendeeInput(
    val seat: Seat,
    val firstName: String = "",
    val lastName: String = ""
) {
    val isComplete: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank()
}

@Immutable
data class Purchase(
    val event: Event,
    val seats: List<Seat>,
    val attendees: List<AttendeeInput>,
    val totalLabel: String,
    val status: String = "Confirmada"
)


package com.eventos.pf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eventos.pf.model.AttendeeInput
import com.eventos.pf.model.Event
import com.eventos.pf.model.Purchase
import com.eventos.pf.model.Seat
import com.eventos.pf.model.SeatStatus
import com.eventos.pf.model.Session

class AppStateHolder(
    private val repository: FakeRepository
) {
    var session by mutableStateOf<Session?>(null)
        private set

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var selectedEvent by mutableStateOf<Event?>(null)
        private set

    var seatMap by mutableStateOf<List<Seat>>(emptyList())
        private set

    var selectedSeats by mutableStateOf<List<Seat>>(emptyList())
        private set

    var attendees by mutableStateOf<List<AttendeeInput>>(emptyList())
        private set

    val history: List<Purchase> get() = repository.getHistory()

    fun login(username: String, password: String, remember: Boolean): Boolean {
        if (password.isBlank()) return false
        val newSession = repository.login(username, remember)
        session = newSession
        if (newSession != null) {
            events = repository.getEvents()
        }
        return newSession != null
    }

    fun logout() {
        session = null
        selectedEvent = null
        seatMap = emptyList()
        selectedSeats = emptyList()
        attendees = emptyList()
        events = emptyList()
    }

    fun selectEvent(eventId: String) {
        val event = repository.getEvent(eventId)
        selectedEvent = event
        if (event != null) {
            seatMap = repository.getSeatMap(event.id)
            selectedSeats = emptyList()
            attendees = emptyList()
        }
    }

    fun toggleSeat(seat: Seat) {
        if (seat.status != SeatStatus.FREE && seat.status != SeatStatus.MINE) return
        val alreadySelected = selectedSeats.any { it.row == seat.row && it.column == seat.column }
        selectedSeats = if (alreadySelected) {
            selectedSeats.filterNot { it.row == seat.row && it.column == seat.column }
        } else {
            if (selectedSeats.size >= 4) selectedSeats else selectedSeats + seat.copy(status = SeatStatus.MINE)
        }
        syncAttendeesWithSeats()
    }

    fun updateAttendee(seat: Seat, first: String, last: String) {
        attendees = attendees.map {
            if (it.seat.row == seat.row && it.seat.column == seat.column) it.copy(firstName = first, lastName = last)
            else it
        }
    }

    fun confirmPurchase(): Purchase? {
        val event = selectedEvent ?: return null
        if (selectedSeats.isEmpty() || attendees.any { !it.isComplete }) return null
        val purchase = repository.savePurchase(event, selectedSeats, attendees)
        return purchase
    }

    fun clearFlow() {
        selectedEvent = null
        seatMap = emptyList()
        selectedSeats = emptyList()
        attendees = emptyList()
    }

    private fun syncAttendeesWithSeats() {
        attendees = selectedSeats.map { seat ->
            attendees.find { it.seat.row == seat.row && it.seat.column == seat.column }
                ?: AttendeeInput(seat = seat)
        }
    }
}


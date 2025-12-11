package com.eventos.pf.data

import com.eventos.pf.model.AttendeeInput
import com.eventos.pf.model.Event
import com.eventos.pf.model.PRICE_PER_SEAT
import com.eventos.pf.model.Presenter
import com.eventos.pf.model.Purchase
import com.eventos.pf.model.Seat
import com.eventos.pf.model.SeatStatus
import com.eventos.pf.model.Session
import kotlin.random.Random

class FakeRepository {
    private val samplePresenters = listOf(
        Presenter("Ana López", "Voz principal"),
        Presenter("DJ Mati", "DJ Set"),
        Presenter("Clara Ruiz", "Productora")
    )

    private val events = listOf(
        Event(
            id = "1",
            title = "Festival Aurora",
            summary = "Indie + Electrónica al atardecer",
            description = "Un line-up curado con artistas emergentes y consagrados en un venue boutique.",
            dateTime = "Sáb 14 Dic · 19:00",
            address = "Parque Central, Ciudad",
            priceLabel = "$45.000 + cargos",
            type = "Festival",
            presenters = samplePresenters,
            seatsAvailable = 28,
            seatsTotal = 36,
            coverUrl = null
        ),
        Event(
            id = "2",
            title = "Standup Nocturno",
            summary = "Humor ácido y música en vivo",
            description = "Una noche de risas y buena compañía. Cupos limitados.",
            dateTime = "Vie 20 Dic · 21:30",
            address = "Teatro Central, Sala 2",
            priceLabel = "$28.000",
            type = "Comedia",
            presenters = listOf(Presenter("Lu Rojo", "Host"), Presenter("Valen Pereyra", "Comediante")),
            seatsAvailable = 18,
            seatsTotal = 24,
            coverUrl = null
        )
    )

    private val history = mutableListOf<Purchase>()

    fun login(username: String, rememberMe: Boolean): Session? {
        if (username.isBlank()) return null
        return Session(username, rememberMe)
    }

    fun getEvents(): List<Event> = events

    fun getEvent(eventId: String): Event? = events.find { it.id == eventId }

    fun getSeatMap(eventId: String): List<Seat> {
        val base = mutableListOf<Seat>()
        val random = Random(eventId.hashCode())
        repeat(6) { row ->
            repeat(6) { col ->
                val noise = random.nextInt(100)
                val status = when {
                    noise < 8 -> SeatStatus.SOLD
                    noise in 8..12 -> SeatStatus.HELD
                    else -> SeatStatus.FREE
                }
                base.add(Seat(row, col, status))
            }
        }
        return base
    }

    fun savePurchase(event: Event, seats: List<Seat>, attendees: List<AttendeeInput>): Purchase {
        val purchase = Purchase(
            event = event,
            seats = seats,
            attendees = attendees,
            totalLabel = seats.size.takeIf { it > 0 }?.let { "$${it * PRICE_PER_SEAT}" } ?: "$0"
        )
        history.add(0, purchase)
        return purchase
    }

    fun getHistory(): List<Purchase> = history.toList()
}


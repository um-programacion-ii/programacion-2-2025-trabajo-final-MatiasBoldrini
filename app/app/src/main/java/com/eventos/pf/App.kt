package com.eventos.pf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.eventos.pf.data.AppStateHolder
import com.eventos.pf.data.FakeRepository
import com.eventos.pf.model.PRICE_PER_SEAT
import com.eventos.pf.model.Seat
import com.eventos.pf.ui.AttendeesScreen
import com.eventos.pf.ui.ConfirmationScreen
import com.eventos.pf.ui.EventDetailScreen
import com.eventos.pf.ui.EventListScreen
import com.eventos.pf.ui.HistoryScreen
import com.eventos.pf.ui.LoginScreen
import com.eventos.pf.ui.SeatSelectionScreen
import com.eventos.pf.ui.theme.EventosTheme

@Composable
fun EventosApp() {
    val navController = rememberNavController()
    val holder = remember { AppStateHolder(FakeRepository()) }
    EventosTheme {
        Scaffold { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                NavHost(
                    navController = navController,
                    startDestination = if (holder.session == null) "login" else "events"
                ) {
                    composable("login") {
                        LoginScreen { user, pass, remember ->
                            if (holder.login(user, pass, remember)) {
                                navController.navigate("events") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                    composable("events") {
                        EventListScreen(
                            events = holder.events,
                            onSelect = {
                                holder.selectEvent(it.id)
                                navController.navigate("detail/${it.id}")
                            },
                            onHistory = { navController.navigate("history") }
                        )
                    }
                    composable(
                        route = "detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                    ) { backStack ->
                        val id = backStack.arguments?.getString("id") ?: return@composable
                        val event = holder.selectedEvent ?: holder.events.find { it.id == id }
                        if (event != null) {
                            holder.selectEvent(event.id)
                            EventDetailScreen(
                                event = event,
                                onBack = { navController.popBackStack() },
                                onSelectSeats = { navController.navigate("seats") }
                            )
                        }
                    }
                    composable("seats") {
                        val seats = holder.seatMap
                        SeatSelectionScreen(
                            seats = seats,
                            selectedSeats = holder.selectedSeats,
                            onBack = { navController.popBackStack() },
                            onToggle = { seat -> holder.toggleSeat(seat) },
                            onContinue = { navController.navigate("attendees") }
                        )
                    }
                    composable("attendees") {
                        AttendeesScreen(
                            attendees = holder.attendees,
                            onBack = { navController.popBackStack() },
                            onChange = { attendee, first, last -> holder.updateAttendee(attendee.seat, first, last) },
                            onContinue = { navController.navigate("confirm") }
                        )
                    }
                    composable("confirm") {
                        val event = holder.selectedEvent ?: return@composable
                        ConfirmationScreen(
                            event = event,
                            seats = holder.selectedSeats.map(Seat::label),
                            attendees = holder.attendees,
                            totalLabel = "$${holder.selectedSeats.size * PRICE_PER_SEAT}",
                            onBack = { navController.popBackStack() },
                            onConfirm = {
                                holder.confirmPurchase()
                                navController.navigate("history") {
                                    popUpTo("events")
                                }
                                holder.clearFlow()
                            }
                        )
                    }
                    composable("history") {
                        HistoryScreen(
                            history = holder.history,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}


package com.example.myapplication.ui.Admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Repository.Notification
import com.example.myapplication.data.Repository.OrderRepository
import com.example.myapplication.data.Repository.UserRepository

@Composable
fun AdminOrdersScreen() {
    var pendingOrders by remember { mutableStateOf(OrderRepository.pending()) }

    fun refresh() { pendingOrders = OrderRepository.pending() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pendingOrders) { order ->
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Commande • ${order.clientName}", style = MaterialTheme.typography.titleMedium)
                    Text("Téléphone: ${order.phone}")
                    Text("Adresse: ${order.address}")

                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            OrderRepository.confirm(order.id)
                            UserRepository.getUser(order.userEmail)?.notifications?.add(
                                Notification(
                                    message = "Votre commande est confirmée ✔. Cliquez ici pour suivre votre commande."
                                )
                            )
                            refresh()
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = "Confirmer", tint = Color.Green)
                        }
                        IconButton(onClick = {
                            OrderRepository.refuse(order.id)
                            UserRepository.getUser(order.userEmail)?.notifications?.add(
                                Notification(
                                    message = "Votre commande a été refusée. Contactez le support."
                                )
                            )
                            refresh()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Refuser", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

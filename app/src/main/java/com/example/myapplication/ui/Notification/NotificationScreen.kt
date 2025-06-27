package com.example.myapplication.ui.Notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.Repository.Notification
import com.example.myapplication.data.Repository.OrderRepository
import com.example.myapplication.data.Repository.UserRepository.User
import com.example.myapplication.navigator.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    user: User,
    onBack: () -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(user.notifications) { n ->
                Card(Modifier.padding(8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(n.message)

                        if ("commande est confirmée" in n.message.lowercase()) {
                            TextButton(onClick = {
                                val order = OrderRepository.forUser(user.email).lastOrNull()
                                if (order != null) {
                                    navController.navigate("order_summary/${order.id}")
                                }
                            }) {
                                Text("Voir ma commande")
                            }
                        }
                    }
                }
                n.read = true
            }
        }
    }
}

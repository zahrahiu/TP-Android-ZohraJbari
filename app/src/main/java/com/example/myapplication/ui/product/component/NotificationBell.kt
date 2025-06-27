package com.example.myapplication.ui.product.component


import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.Composable
import com.example.myapplication.data.Repository.UserRepository.User


@Composable
fun NotificationBell(
    user: User,
    onClick: () -> Unit
) {
    val unread = user.notifications.count { !it.read }

    BadgedBox(
        badge = {
            if (unread > 0) {
                Badge { Text("$unread") }
            }
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications"
            )
        }
    }
}

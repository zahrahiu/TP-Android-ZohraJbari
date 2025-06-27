// UserRepository.kt
package com.example.myapplication.data.Repository

import java.util.UUID

enum class UserStatus { PENDING, ACCEPTED, REFUSED }

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    var read: Boolean = false,
    val orderId: String? = null
)

object UserRepository {

    data class User(
        val email: String,
        val password: String,
        val nom: String,
        val prenom: String,
        val telephone: String,
        val isAdmin: Boolean = false,
        var status: UserStatus = UserStatus.PENDING,
        val notifications: MutableList<Notification> = mutableListOf()
    )

    private val users = mutableMapOf(
        "admin@gmail.com" to User(
            email = "admin@gmail.com",
            password = "admin",
            nom = "Admin",
            prenom = "Admin",
            telephone = "0000000000",
            isAdmin = true,
            status = UserStatus.ACCEPTED
        )
    )

    fun allUsers() = users.values.toList()
    fun pendingUsers() = users.values.filter { it.status == UserStatus.PENDING }
    fun acceptedUsers() = users.values.filter { it.status == UserStatus.ACCEPTED }
    fun refusedUsers() = users.values.filter { it.status == UserStatus.REFUSED }

    fun acceptUser(email: String) {
        users[email]?.status = UserStatus.ACCEPTED
    }

    fun refuseUser(email: String, msg: String) {
        users[email]?.apply {
            status = UserStatus.REFUSED
            notifications += Notification(message = msg)
        }
    }

    fun register(
        email: String,
        password: String,
        nom: String,
        prenom: String,
        telephone: String
    ): Boolean {
        val user = users[email]
        if (user != null) {
            if (user.status == UserStatus.REFUSED) return false
            return false
        }
        users[email] = User(email, password, nom, prenom, telephone)
        return true
    }


    fun login(email: String, password: String): User? {
        val user = users[email] ?: return null
        return if (user.password == password && user.status != UserStatus.REFUSED) user else null
    }


    fun getUser(email: String): User? = users[email]
}

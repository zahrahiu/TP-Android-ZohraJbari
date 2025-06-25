package com.example.myapplication.data.Repository

object UserRepository {

    data class User(
        val email: String,
        val password: String,
        val nom: String,
        val prenom: String,
        val telephone: String
    )

    private val users = mutableMapOf<String, User>()


    fun register(
        email: String,
        password: String,
        nom: String,
        prenom: String,
        telephone: String
    ): Boolean {
        if (users.containsKey(email)) return false
        users[email] = User(email, password, nom, prenom, telephone)
        return true
    }


    fun login(email: String, password: String): Boolean =
        users[email]?.password == password


    fun getUser(email: String): User? = users[email]
}

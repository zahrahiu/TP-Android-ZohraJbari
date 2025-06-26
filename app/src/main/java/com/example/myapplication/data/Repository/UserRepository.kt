package com.example.myapplication.data.Repository

object UserRepository {

    data class User(
        val email: String,
        val password: String,
        val nom: String,
        val prenom: String,
        val telephone: String,
        val isAdmin: Boolean = false
    )



    private val users = mutableMapOf(
        "admin@gmail.com" to User(
            email = "admin@gmail.com",
            password = "admin",
            nom = "Admin",
            prenom = "Admin",
            telephone = "0000000000",
            isAdmin = true
        )
    )

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




    fun login(email: String, password: String): User? =
        users[email]?.takeIf { it.password == password }


    fun getUser(email: String): User? = users[email]
}

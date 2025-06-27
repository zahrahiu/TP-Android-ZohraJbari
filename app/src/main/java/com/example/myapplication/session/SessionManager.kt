package com.example.myapplication.session

import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.data.Repository.UserRepository.User

object SessionManager {
    val currentUser = mutableStateOf<User?>(null)
}
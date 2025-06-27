package com.example.myapplication.ui.Admin

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Repository.UserRepository
import com.example.myapplication.data.Repository.UserRepository.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor() : ViewModel() {

    /** Utilisateurs en attente de validation */
    var pending = mutableStateListOf<User>()
        private set

    /** Utilisateurs déjà acceptés */
    var accepted = mutableStateListOf<User>()
        private set

    var refused  = mutableStateListOf<User>()
        private set

    init { refresh() }

    /** Recharge les listes depuis le dépôt */
    fun refresh() {
        pending.clear();   pending += UserRepository.pendingUsers()
        accepted.clear();  accepted += UserRepository.acceptedUsers()
    }

    /** Accepter un utilisateur  */
    fun accept(email: String) {
        UserRepository.acceptUser(email)
        refresh()
    }

    /** Refuser un utilisateur et lui envoyer une notification */
    fun refuse(email: String) {
        UserRepository.refuseUser(
            email,
            "Vos informations n’ont pas été validées. 👉 Cliquez ici pour les corriger et refaire votre inscription."
        )
        refresh()
    }
}

package com.example.buscardapp

import androidx.lifecycle.ViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val auth = SupabaseClient.supabase.auth

    private val _userEmail = MutableStateFlow(auth.currentUserOrNull()?.email ?: "Utilizador")
    val userEmail: StateFlow<String> = _userEmail

    fun signOut() {
        // Podes chamar o signOut diretamente aqui
        kotlinx.coroutines.MainScope().launch {
            auth.signOut()
        }
    }
}
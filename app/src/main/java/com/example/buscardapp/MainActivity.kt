package com.example.buscardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.buscardapp.ui.theme.BusCardAppTheme

class MainActivity : ComponentActivity() {

    // Instancia o teu ViewModel que já tens pronto
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BusCardAppTheme {
                // Chamamos o NavGraph que vai gerir se mostra Login ou Home
                NavGraph(authViewModel)
            }
        }
    }
}
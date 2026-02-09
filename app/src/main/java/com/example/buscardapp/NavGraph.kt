package com.example.buscardapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun NavGraph(authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.collectAsState()

    when {
        // Se o login for bem sucedido, entra na App Principal (Home/Rotas/Perfil)
        authState == "Login efetuado!" || authState == "Bem-vindo!" -> {
            MainScreen()
        }

        // Se NÃO estiver logado, mostra o ecrã de Login/Registo
        else -> {
            // ATENÇÃO: Substitui 'AuthScreen' pelo nome da tua função de Login
            // Se a tua função de login estiver noutro ficheiro,
            // certifica-te que ela NÃO é 'private'.
            AuthScreen(authViewModel)
        }
    }
}
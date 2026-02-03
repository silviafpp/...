package com.example.buscardapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(false) } // False = Registo, True = Login

    val context = androidx.compose.ui.platform.LocalContext.current
    val message by authViewModel.authState.collectAsState()
    val userExists by authViewModel.userExists.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Login" else "Criar Conta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botão Google
        Button(
            onClick = { authViewModel.signInWithGoogle(context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(if (isLoginMode) "Entrar com Google" else "Registar com Google")
        }

        Text(text = "ou", modifier = Modifier.padding(vertical = 16.dp))

        // Campos de Texto (Só aparecem se não estiver a usar Google diretamente)
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Principal Manual
        // Na tua AuthScreen, dentro da Column:
        Button(
            onClick = {
                if (isLoginMode) {
                    authViewModel.signInWithGoogle(context) // Login normal (deixa entrar sempre)
                } else {
                    authViewModel.signUpWithGoogle(context) // Registo (bloqueia se já existir)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Entrar com Google" else "Registar com Google")
        }

        // --- LÓGICA DE VERIFICAÇÃO ---
        if (userExists) {
            Spacer(modifier = Modifier.height(10.dp))
            // Aviso de que o user já existe
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = message ?: "", color = MaterialTheme.colorScheme.onErrorContainer)

                    // Botão para forçar a ida para a página de Login
                    TextButton(onClick = { isLoginMode = true }) {
                        Text("Ir para página de Login")
                    }
                }
            }
        } else {
            message?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        }

        // Alternar entre telas manualmente
        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(if (isLoginMode) "Não tens conta? Regista-te" else "Já tens conta? Faz login")
        }
    }
}
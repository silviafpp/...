package com.example.buscardapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(authViewModel: AuthViewModel) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current // Necessário para o Credential Manager

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLogin) "Entrar" else "Registo", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(20.dp))

        if (authState?.contains("Verifique") == true) {
            OutlinedTextField(value = otpCode, onValueChange = { otpCode = it }, label = { Text("Código OTP") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { authViewModel.verifyOtp(email, otpCode) }, modifier = Modifier.fillMaxWidth()) { Text("Verificar") }
        } else {
            if (!isLogin) {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Primeiro Nome") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Último Nome") }, modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

            Button(
                onClick = {
                    if (isLogin) authViewModel.signInWithEmail(email, password)
                    else authViewModel.signUpWithEmail(email, password, firstName, lastName)
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(if (isLogin) "Entrar com Email" else "Registar com Email")
            }

            // --- NOVO BOTÃO GOOGLE ---
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { authViewModel.signInWithGoogle(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Entrar com Google" else "Registar com Google")
            }
            // -------------------------

            TextButton(onClick = { isLogin = !isLogin }) {
                Text(if (isLogin) "Ainda não tem conta? Registe-se" else "Já tem conta? Entre")
            }
        }
        authState?.let { Text(it, modifier = Modifier.padding(top = 10.dp)) }
    }
}
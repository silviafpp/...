package com.example.buscardapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(viewModel: UserViewModel) {
    val email by viewModel.userEmail.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = Color.LightGray) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(25.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Perfil do Utilizador", style = MaterialTheme.typography.titleLarge)
        Text(email, color = Color.Gray)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.signOut() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Terminar Sessão")
        }
    }
}
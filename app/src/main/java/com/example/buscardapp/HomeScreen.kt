package com.example.buscardapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()
    val user = SupabaseClient.supabase.auth.currentUserOrNull()

    // Extração segura dos metadados
    val firstName = user?.userMetadata?.get("first_name")?.toString()?.removeSurrounding("\"") ?: "Utilizador"
    val lastName = user?.userMetadata?.get("last_name")?.toString()?.removeSurrounding("\"") ?: ""

    var activeCard by remember { mutableStateOf<UserCard?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showInitialDialog by remember { mutableStateOf(false) }
    var showTypeDialog by remember { mutableStateOf(false) }

    // Carregar cartão da base de dados
    LaunchedEffect(Unit) {
        try {
            val card = SupabaseClient.supabase.postgrest["user_cards"]
                .select()
                .decodeSingleOrNull<UserCard>()
            activeCard = card
        } catch (e: Exception) {
            println("Erro ao carregar cartão: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Olá, $firstName $lastName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (activeCard == null) {
            // Botão Adicionar (Corrigido o Modifier.size)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEEEEEE))
                    .clickable { showInitialDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp), // Correção aqui
                        tint = Color.Gray
                    )
                    Text("Adicionar Cartão", color = Color.Gray)
                }
            }
        } else {
            // Cartão Digital
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(activeCard?.card_type?.uppercase() ?: "", color = Color.Cyan, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("$firstName $lastName".uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("SÃO MIGUEL TRANSPORTES", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }
    }

    // --- DIÁLOGOS ---
    if (showInitialDialog) {
        AlertDialog(
            onDismissRequest = { showInitialDialog = false },
            title = { Text("Novo Cartão") },
            text = { Text("Deseja criar um cartão digital para as rotas de São Miguel?") },
            confirmButton = {
                Button(onClick = { showInitialDialog = false; showTypeDialog = true }) { Text("Criar Digital") }
            },
            dismissButton = { TextButton(onClick = { showInitialDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            title = { Text("Escolha o tipo de Passe") },
            confirmButton = {},
            text = {
                Column {
                    listOf("Diário", "Semanal", "Mensal").forEach { tipo ->
                        ListItem(
                            headlineContent = { Text(tipo) },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    val newCard = UserCard(user_id = user?.id ?: "", card_type = tipo)
                                    SupabaseClient.supabase.postgrest["user_cards"].insert(newCard)
                                    activeCard = newCard
                                    showTypeDialog = false
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
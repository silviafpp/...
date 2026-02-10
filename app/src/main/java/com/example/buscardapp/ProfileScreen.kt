package com.example.buscardapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // 1. Estados para os dados vindos do Supabase
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var userCard by remember { mutableStateOf<UserCard?>(null) }
    var userEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // 2. Lógica para carregar os dados
    LaunchedEffect(Unit) {
        try {
            val user = SupabaseClient.supabase.auth.currentUserOrNull()
            if (user != null) {
                userEmail = user.email ?: "" // Email vem diretamente da conta
                val uid = user.id

                // Busca o nome real na tabela 'profiles' (onde o trigger inseriu os dados)
                userProfile = SupabaseClient.supabase.postgrest["profiles"]
                    .select { filter { eq("id", uid) } }
                    .decodeSingleOrNull<UserProfile>()

                // Busca os dados do cartão (saldo/viagens)
                userCard = SupabaseClient.supabase.postgrest["user_cards"]
                    .select { filter { eq("user_id", uid) } }
                    .decodeSingleOrNull<UserCard>()
            }
        } catch (e: Exception) {
            println("Erro ao carregar perfil: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // 3. Processamento do Nome (Concatena Primeiro + Último)
    val nomeExibicao = if (userProfile != null && !userProfile?.firstName.isNullOrBlank()) {
        "${userProfile?.firstName} ${userProfile?.lastName}"
    } else {
        "Utilizador" // Fallback caso a tabela ainda esteja vazia
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF006D4E))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkMode) Color(0xFF121212) else Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            // CABEÇALHO VERDE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFF006D4E), Color(0xFF004D36))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar com a inicial do utilizador
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nomeExibicao.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            // NOME REAL VINDO DA DB
                            Text(
                                text = nomeExibicao,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // EMAIL VINDO DO AUTH
                            Text(
                                text = userEmail,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // CARDS DE SALDO E VIAGENS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-40).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(Modifier.weight(1f), "${userCard?.saldo ?: 0.0}€", "Saldo", Icons.Default.AccountBalanceWallet)
                InfoCard(Modifier.weight(1f), "${userCard?.tripsLeft ?: 0}", "Viagens", Icons.Default.ConfirmationNumber)
            }

            // MENU DE CONFIGURAÇÕES
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "GERIR CONTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // MODO ESCURO
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Brightness4, null, tint = Color(0xFF006D4E))
                        Text(
                            "Modo Escuro",
                            Modifier.padding(start = 12.dp).weight(1f),
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                        Switch(checked = isDarkMode, onCheckedChange = onThemeChange)
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                    // SAIR (CHAMA A FUNÇÃO DO TEU VIEWMODEL)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { authViewModel.logout() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Logout, null, tint = Color.Red)
                        Text("Terminar Sessão", color = Color.Red, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(modifier: Modifier, value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = Color(0xFF006D4E), modifier = Modifier.size(24.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
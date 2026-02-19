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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onCardClick: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showOptionsSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    fun carregarPerfil() {
        scope.launch {
            isLoading = true
            try {
                val user = SupabaseClient.supabase.auth.currentUserOrNull()
                if (user != null) {
                    userProfile = SupabaseClient.supabase.postgrest["profiles"]
                        .select {
                            filter { eq("id", user.id) }
                        }
                        .decodeSingleOrNull<UserProfile>()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        carregarPerfil()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF121212) else Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = "O meu cartão",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF006D4E))
            }
        } else if (userProfile?.hasCard == true) {
            // --- NOVO DESIGN DO CARTÃO ATIVO (COLORIDO) ---
            val cardColor = if (userProfile?.cardType == "STUDENT") Color(0xFF1976D2) else Color(0xFF006D4E)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(cardColor, cardColor.copy(alpha = 0.8f))),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onCardClick() }
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Topo: Título e Badge "Active"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Azores Bus Card", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Active", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Saldo (Podes ligar isto futuramente à DB)
                    Text(
                        text = "€45.80",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Rodapé com Dados Dinâmicos da DB
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Card Type", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            Text(
                                text = userProfile?.cardType?.replaceFirstChar { it.uppercase() } ?: "Normal",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("User Name", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            Text(
                                text = "${userProfile?.firstName ?: ""} ${userProfile?.lastName ?: ""}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // --- CARTÃO VAZIO (CINZENTO) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { showOptionsSheet = true },
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Criar novo cartão", color = Color.Gray)
                    }
                }
            }
        }
    }

    // --- ABA DE OPÇÕES (MODAL BOTTOM SHEET) ---
    if (showOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showOptionsSheet = false },
            sheetState = sheetState,
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "Escolha o tipo de passe",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OptionItem("Diário", "Válido por 24h", Icons.Default.Today) {
                    userViewModel.criarCartao("DIARIO") { carregarPerfil() }
                    showOptionsSheet = false
                }
                OptionItem("Semanal", "Válido por 7 dias", Icons.Default.ViewWeek) {
                    userViewModel.criarCartao("SEMANAL") { carregarPerfil() }
                    showOptionsSheet = false
                }
                OptionItem("Mensal", "Válido por 30 dias", Icons.Default.CalendarMonth) {
                    userViewModel.criarCartao("MENSAL") { carregarPerfil() }
                    showOptionsSheet = false
                }
            }
        }
    }
}

@Composable
fun OptionItem(title: String, desc: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF006D4E), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = desc, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}
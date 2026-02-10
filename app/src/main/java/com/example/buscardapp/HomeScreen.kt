package com.example.buscardapp

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onCardClick: () -> Unit) {
    // Usamos dados de teste (Hardcoded) para garantir que o cartão aparece
    val cardType = "Semanal"
    val tripsLeft = 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("O meu Passe", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        // CARTÃO DIGITAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    Log.d("FLUXO", "0. Clique na HomeScreen")
                    onCardClick()
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(Icons.Default.Nfc, null, tint = Color.Cyan)
                    Text(cardType.uppercase(), color = Color.Cyan, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("$tripsLeft VIAGENS RESTANTES", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DASHBOARD ESTATÍSTICAS
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox(Modifier.weight(1f), "Viagens", "12", Icons.Default.ConfirmationNumber)
            StatBox(Modifier.weight(1f), "Minutos", "450", Icons.Default.Timer)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Viagens Recentes", fontWeight = FontWeight.Bold)
        RecentTripItem("Ponta Delgada", "Ribeira Grande", "14:20")
    }
}

// FUNÇÕES AUXILIARES SEMPRE FORA
@Composable
fun StatBox(modifier: Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, modifier = Modifier.size(20.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun RecentTripItem(from: String, to: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Icon(Icons.Default.History, null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("$from → $to", fontWeight = FontWeight.Medium)
            Text(time, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CreateCardPlaceholder(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onClick() }) {
        Text("Adicionar Cartão")
    }
}
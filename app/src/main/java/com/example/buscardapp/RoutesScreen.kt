package com.example.buscardapp

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.postgrest.postgrest

@Composable
fun RoutesScreen() {
    val scope = rememberCoroutineScope()
    var routesList by remember { mutableStateOf(listOf<BusRoute>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Estados para os dropdowns
    var originSelected by remember { mutableStateOf("Selecionar Origem") }
    var destSelected by remember { mutableStateOf("Selecionar Destino") }

    // Carregar rotas ao abrir a tela
    LaunchedEffect(Unit) {
        try {
            val results = SupabaseClient.supabase.postgrest["bus_routes"]
                .select().decodeList<BusRoute>()
            routesList = results
        } catch (e: Exception) {
            // Tratar erro
        } finally {
            isLoading = false
        }
    }

    // Filtrar opções únicas para os dropdowns
    val origins = routesList.map { it.origin }.distinct()
    val destinations = routesList.filter { it.origin == originSelected }.map { it.destination }.distinct()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Rotas São Miguel", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Caixa de Seleção
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RouteDropdown("Partida", origins, originSelected) { originSelected = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    RouteDropdown("Destino", destinations, destSelected) { destSelected = it }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resultado da Pesquisa
            Text("Viagens Disponíveis", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
                val filteredRoutes = routesList.filter {
                    it.origin == originSelected && it.destination == destSelected
                }

                items(filteredRoutes) { route ->
                    RouteItem(route)
                }
            }
        }
    }
}

@Composable
fun RouteDropdown(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(selected)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RouteItem(route: BusRoute) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color(0xFF2D3142))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${route.origin} → ${route.destination}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("Duração: ${route.duration ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
            }
            Text("€${route.price}", color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}
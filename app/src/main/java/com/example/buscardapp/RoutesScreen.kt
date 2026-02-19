package com.example.buscardapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

@Composable
fun RoutesScreen() {
    // ESTADOS
    var routesList by remember { mutableStateOf(listOf<BusRoute>()) }
    var originSelected by remember { mutableStateOf("") }
    var destSelected by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // 1. CARREGAR DADOS DA DB
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val results = SupabaseClient.supabase.postgrest["bus_routes"]
                    .select().decodeList<BusRoute>()
                routesList = results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // 2. LÓGICA DE FILTRAGEM
    val origins = routesList.map { it.origin }.distinct().sorted()
    val destinations = routesList
        .filter { originSelected == "" || it.origin == originSelected }
        .map { it.destination }
        .distinct()
        .sorted()

    val filteredRoutes = routesList.filter {
        val matchOrigin = originSelected == "" || it.origin == originSelected
        val matchDest = destSelected == "" || it.destination == destSelected
        matchOrigin && matchDest
    }

    // --- UI PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp)
    ) {
        Text(
            text = "Explorar Rotas",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D5A41)
        )
        Text(text = "Selecione o seu trajeto", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // --- CARD DE SELEÇÃO (VISUAL PREMIUM) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // SELETOR PARTIDA
                CustomRouteSelector(
                    label = "Partida",
                    selected = if (originSelected == "") "Selecionar Origem" else originSelected,
                    options = origins
                ) {
                    originSelected = it
                    destSelected = "" // Reseta destino para nova origem
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SELETOR CHEGADA
                CustomRouteSelector(
                    label = "Chegada",
                    selected = if (destSelected == "") "Selecionar Destino" else destSelected,
                    options = destinations,
                    enabled = originSelected != ""
                ) {
                    destSelected = it
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- LISTAGEM DE RESULTADOS ---
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2D5A41))
            }
        } else {
            if (filteredRoutes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma rota disponível", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredRoutes) { route ->
                        RouteItemDesign(route)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomRouteSelector(
    label: String,
    selected: String,
    options: List<String>,
    enabled: Boolean = true,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(16.dp))
                .clickable(enabled = enabled) { expanded = true }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (enabled) Color(0xFF2D5A41) else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selected,
                    color = if (selected.contains("Selecionar")) Color.LightGray else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
            ) {
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
fun RouteItemDesign(route: BusRoute) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F5))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = Color(0xFF2D5A41),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${route.origin} → ${route.destination}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Linha ${route.route_number} • ${route.duration}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "${"%.2f".format(route.price)}€",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF2D5A41)
            )
        }
    }
}
package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEstadioScreen(
    estadioId: Int,
    viewModel: QuinielaViewModel,
    onVolver: () -> Unit
) {
    val estado = viewModel.estado.collectAsState().value

    // --- SOLUCIÓN A LA CARGA INFINITA ---
    // Si entramos a esta pantalla y las listas están vacías, las descargamos
    LaunchedEffect(estadioId) {
        if (estado.listaEstadios.isEmpty()) {
            viewModel.cargarEstadiosYPredicciones()
        }
        if (estado.listaCompletaPartidos.isEmpty()) {
            viewModel.cargarTodosLosPartidos()
        }
    }

    // Buscamos el estadio exacto
    val estadio = estado.listaEstadios.find { it.id == estadioId }

    // Filtramos los partidos para mostrar solo los que se juegan aquí
    val partidosEnEsteEstadio = estado.listaCompletaPartidos.filter { it.stadium_id == estadioId }

    Scaffold(
        containerColor = Color(0xFF121A16),
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Estadio", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121A16))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Si el estadio ya se descargó, lo mostramos
            if (estadio != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A22)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(estadio.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFA5D6A7))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${estadio.city}, ${estadio.country}", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFFA5D6A7))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Capacidad: ${estadio.capacity} espectadores", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text("Partidos programados", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                if (partidosEnEsteEstadio.isEmpty()) {
                    Text("Aún no hay partidos programados en esta sede.", color = Color.Gray)
                } else {
                    partidosEnEsteEstadio.forEach { partido ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A22))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${partido.home_team} vs ${partido.away_team}", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(partido.match_date.replace("T", " ").take(16), color = Color.Gray)
                            }
                        }
                    }
                }
            } else {
                // Si todavía no se descargó, mostramos el círculo de carga mientras esperamos
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFA5D6A7))
                }
            }
        }
    }
}
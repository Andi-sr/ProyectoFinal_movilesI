package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePartidoScreen(
    partidoId: Int,
    viewModel: QuinielaViewModel,
    onVolver: () -> Unit
) {
    // Variables temporales para el selector de goles
    var golesLocal by remember { mutableIntStateOf(0) }
    var golesVisitante by remember { mutableIntStateOf(0) }
    val partido = viewModel.estado.collectAsState().value
        .listaCompletaPartidos
        .find { it.id == partidoId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121A16))
            .padding(16.dp)
    ) {
        // HEADER CON BOTÓN DE VOLVER
        IconButton(onClick = onVolver, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }

        Text("Detalle del Partido", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            "${partido?.home_team ?: ""} vs ${partido?.away_team ?: ""}",
            color = Color(0xFFA5D6A7),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        // TARJETA DE PRONÓSTICO
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A22)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("REGISTRA TU PRONÓSTICO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // EQUIPO LOCAL
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFF2E4035))) {
                            Text(
                                partido?.home_team ?: "LOCAL",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )}
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectorGoles(goles = golesLocal, onCambiarGoles = { golesLocal = it })
                    }

                    Text("VS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

                    // EQUIPO VISITANTE
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFF2E4035))) {
                            Text(
                                partido?.away_team ?: "VISITA",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )}
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectorGoles(goles = golesVisitante, onCambiarGoles = { golesVisitante = it })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÓN GUARDAR
                Button(
                    onClick = {
                        onVolver()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GUARDAR PRONÓSTICO")
                }
            }
        }
    }
}

// Componente reutilizable para los botones de + y -
@Composable
fun SelectorGoles(goles: Int, onCambiarGoles: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { if (goles > 0) onCambiarGoles(goles - 1) },
            modifier = Modifier.size(32.dp).background(Color(0xFF2E4035), CircleShape)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "-", tint = Color.White, modifier = Modifier.size(16.dp))
        }

        Text(
            text = goles.toString(),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { onCambiarGoles(goles + 1) },
            modifier = Modifier.size(32.dp).background(Color(0xFF388E3C), CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "+", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}
package com.example.proyectofinal_movilesi.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePartidoScreen(
    partidoId: Int,
    viewModel: QuinielaViewModel,
    onVolver: () -> Unit,
    onVerEstadio: (Int) -> Unit
) {
    val contexto = LocalContext.current
    val estado = viewModel.estado.collectAsState().value
    val partido = estado.listaCompletaPartidos.find { it.id == partidoId }

    val prediccionPrevia = estado.misPredicciones.find { it.match_id == partidoId }

    var golesLocal by remember { mutableIntStateOf(prediccionPrevia?.home_score ?: 0) }
    var golesVisitante by remember { mutableIntStateOf(prediccionPrevia?.away_score ?: 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121A16))
            .padding(16.dp)
    ) {
        IconButton(onClick = onVolver, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }

        Text("Detalle del Partido", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            "${partido?.home_team ?: ""} vs ${partido?.away_team ?: ""}",
            color = Color(0xFFA5D6A7),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fecha: ${partido?.match_date?.take(10) ?: "-"}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Hora: ${partido?.match_date?.drop(11)?.take(5) ?: "-"}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fase: ${partido?.phase ?: "-"}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Estado: ${partido?.status ?: "-"}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                if (partido?.home_score != null && partido.away_score != null) {
                    Text("Resultado oficial", color = Color(0xFFA5D6A7), fontWeight = FontWeight.Bold)
                    Text(
                        "${partido.home_score} - ${partido.away_score}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // AQUÍ ESTÁ EL CÓDIGO DEL BOTÓN CORREGIDO
        Button(
            onClick = {
                val idEstadio = partido?.stadium_id ?: 1
                onVerEstadio(idEstadio)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4035)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Estadio", tint = Color(0xFFA5D6A7))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Detalles del Estadio", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A22)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("REGISTRA TU PRONÓSTICO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Pronóstico actual: $golesLocal - $golesVisitante",
                    color = Color(0xFFA5D6A7),
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                val partidoFinalizado = partido?.home_score != null && partido.away_score != null

                Button(
                    enabled = !partidoFinalizado,
                    onClick = {
                        viewModel.guardarPronostico(
                            partidoId = partidoId,
                            golesLocal = golesLocal,
                            golesVisitante = golesVisitante,
                            onExito = {
                                Toast.makeText(contexto, "Pronóstico guardado exitosamente", Toast.LENGTH_SHORT).show()
                                onVolver()
                            },
                            onError = { mensaje ->
                                Toast.makeText(contexto, "Fallo: $mensaje", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "GUARDAR PRONÓSTICO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (partidoFinalizado) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Este partido ya finalizó. El pronóstico ya no puede modificarse.",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

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
package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.data.PartidoResponse
import com.example.proyectofinal_movilesi.viewmodel.QuinielaState
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.lazy.items

val ColorFondoPartidos = Color(0xFF121A16)
val ColorTarjetaPartido = Color(0xFF1B2A22)
val ColorBotonSeleccionado = Color(0xFF388E3C)
val ColorBotonInactivo = Color(0xFF1E2B24)
val ColorTextoGris = Color(0xFFAAAAAA)
val ColorAcentoVerde = Color(0xFFA5D6A7)

@Composable
fun PartidosScreen(
    estado: QuinielaState,
    viewModel: QuinielaViewModel,
    onNavegarDetallePartido: (Int) -> Unit
) {
    // AQUÍ ESTÁ EL CICLO DE TIEMPO REAL
    LaunchedEffect(Unit) {
        viewModel.cargarTodosLosPartidos()
        while(true) {
            kotlinx.coroutines.delay(30000) // Actualiza cada 30 segundos
            viewModel.sincronizarPartidosEnTiempoReal()
        }
    }

    var faseSeleccionada by remember { mutableStateOf("Todos") }
    var estadoSeleccionado by remember { mutableStateOf("Todos") }
    var indiceSemanaSeleccionada by remember { mutableIntStateOf(0) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }

    val semanasTorneo = remember {
        val inicio = LocalDate.of(2026, 6, 11)
        val fin = LocalDate.of(2026, 7, 19)
        val diasTotales = ChronoUnit.DAYS.between(inicio, fin).toInt()
        val todosLosDias = (0..diasTotales).map { inicio.plusDays(it.toLong()) }
        todosLosDias.chunked(7)
    }

    val partidosFiltrados = estado.listaCompletaPartidos.filter { partido ->
        val pasaFase = if (faseSeleccionada == "Todos") {
            true
        } else {
            when (faseSeleccionada) {
                "Grupos" -> partido.phase.contains("GROUP", ignoreCase = true)
                "16vos" -> partido.phase.contains("ROUND_OF_32", ignoreCase = true)
                "Octavos" -> partido.phase.contains("ROUND_OF_16", ignoreCase = true)
                "Cuartos" -> partido.phase.contains("QUARTER", ignoreCase = true)
                "Semis" -> partido.phase.contains("SEMI", ignoreCase = true)
                "Final" -> partido.phase.contains("FINAL", ignoreCase = true) && !partido.phase.contains("QUARTER", ignoreCase = true)
                else -> true
            }
        }

        val pasaFecha = if (fechaSeleccionada == null) {
            true
        } else {
            partido.match_date.startsWith(fechaSeleccionada.toString())
        }

        val pasaEstado = if (estadoSeleccionado == "Todos") {
            true
        } else {
            val fechaPartido = partido.match_date.substringBefore("T")
            val hoyStr = LocalDate.now().toString()

            when (estadoSeleccionado) {
                "Finalizados" -> fechaPartido < hoyStr
                "Próximos" -> fechaPartido >= hoyStr
                else -> true
            }
        }

        pasaFase && pasaFecha && pasaEstado
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondoPartidos)
            .padding(top = 16.dp)
    ) {
        FiltrosSuperiores(
            faseActual = faseSeleccionada,
            onCambiarFase = {
                faseSeleccionada = it
                if (it != "Todos") {
                    fechaSeleccionada = null
                }
            },
            semanasGeneradas = semanasTorneo,
            semanaActualIndex = indiceSemanaSeleccionada,
            onCambiarSemana = { nuevaSemana ->
                indiceSemanaSeleccionada = nuevaSemana
                fechaSeleccionada = null
                faseSeleccionada = "Todos"
            },
            fechaActual = fechaSeleccionada,
            onCambiarFecha = {
                fechaSeleccionada = if (fechaSeleccionada == it) null else it
                if (fechaSeleccionada != null) {
                    faseSeleccionada = "Todos"
                }
            },
            estadoActual = estadoSeleccionado,
            onCambiarEstado = { estadoSeleccionado = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = if (partidosFiltrados.isEmpty()) "No hay partidos para este filtro" else "Calendario",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            items(partidosFiltrados) { partido ->
                TarjetaPartidoUI(
                    partido = partido,
                    estado = estado,
                    onClick = { onNavegarDetallePartido(partido.id) }
                )
            }
        }
    }
}

@Composable
fun FiltrosSuperiores(
    faseActual: String,
    onCambiarFase: (String) -> Unit,
    semanasGeneradas: List<List<LocalDate>>,
    semanaActualIndex: Int,
    onCambiarSemana: (Int) -> Unit,
    fechaActual: LocalDate?,
    onCambiarFecha: (LocalDate) -> Unit,
    estadoActual: String,
    onCambiarEstado: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val fases = listOf("Todos", "Grupos", "16vos", "Octavos", "Cuartos", "Semis", "Final")
            items(fases.size) { index ->
                val fase = fases[index]
                BotonFiltro(texto = fase, seleccionado = fase == faseActual, onClick = { onCambiarFase(fase) })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(semanasGeneradas.size) { index ->
                BotonFiltro(
                    texto = "Semana ${index + 1}",
                    seleccionado = index == semanaActualIndex,
                    onClick = { onCambiarSemana(index) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val diasDeLaSemanaSeleccionada = semanasGeneradas.getOrNull(semanaActualIndex) ?: emptyList()
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diasDeLaSemanaSeleccionada.size) { index ->
                val fecha = diasDeLaSemanaSeleccionada[index]
                TarjetaFechaFiltro(
                    fecha = fecha,
                    seleccionado = fecha == fechaActual,
                    onClick = { onCambiarFecha(fecha) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val estados = listOf("Todos", "Finalizados", "Próximos")
            items(estados.size) { index ->
                val estadoOpcion = estados[index]
                BotonFiltro(texto = estadoOpcion, seleccionado = estadoOpcion == estadoActual, onClick = { onCambiarEstado(estadoOpcion) })
            }
        }
    }
}

@Composable
fun BotonFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (seleccionado) ColorBotonSeleccionado else ColorBotonInactivo,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color.White else ColorTextoGris,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TarjetaFechaFiltro(fecha: LocalDate, seleccionado: Boolean, onClick: () -> Unit) {
    val mesTexto = when (fecha.monthValue) {
        1 -> "ENE"; 2 -> "FEB"; 3 -> "MAR"; 4 -> "ABR"; 5 -> "MAY"; 6 -> "JUN"
        7 -> "JUL"; 8 -> "AGO"; 9 -> "SEP"; 10 -> "OCT"; 11 -> "NOV"; 12 -> "DIC"
        else -> ""
    }

    Column(
        modifier = Modifier
            .background(
                color = if (seleccionado) ColorBotonSeleccionado else ColorBotonInactivo,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = mesTexto, color = if (seleccionado) Color.White else ColorTextoGris, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = fecha.dayOfMonth.toString(), color = if (seleccionado) Color.White else ColorTextoGris, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun TarjetaPartidoUI(partido: PartidoResponse, estado: QuinielaState, onClick: () -> Unit) {

    // Buscamos si el usuario tiene una predicción para este partido
    val prediccionPrevia = estado.misPredicciones.find { it.match_id == partido.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ColorTarjetaPartido),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${partido.phase.uppercase().replace("_", " ")}",
                    color = ColorTextoGris,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = ColorTextoGris, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ColorAcentoVerde))
                Spacer(modifier = Modifier.width(6.dp))

                val fechaMostrada = if (partido.match_date.contains("T")) {
                    val fecha = partido.match_date.substringBefore("T")
                    val hora = partido.match_date.substringAfter("T").take(5)
                    "$fecha • $hora"
                } else {
                    partido.match_date.take(16)
                }

                Text(
                    text = "PARTIDO • $fechaMostrada",
                    color = ColorAcentoVerde,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2E4035))) {
                        Text(partido.home_team.take(3).uppercase(), modifier = Modifier.align(Alignment.Center), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(partido.home_team, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    if (partido.home_score != null && partido.away_score != null) {
                        Text("${partido.home_score} - ${partido.away_score}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    } else {
                        Text("VS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.background(Color(0xFF2E4035), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                        // MOSTRAMOS LA PREDICCIÓN REAL AQUÍ
                        val textoPrediccion = if (prediccionPrevia != null) {
                            "${prediccionPrevia.home_score} - ${prediccionPrevia.away_score}"
                        } else {
                            "-"
                        }
                        Text("Predicción: $textoPrediccion", color = ColorAcentoVerde, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2E4035))) {
                        Text(partido.away_team.take(3).uppercase(), modifier = Modifier.align(Alignment.Center), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(partido.away_team, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
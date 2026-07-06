package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.data.ApiConexion.GrupoResponse
import com.example.proyectofinal_movilesi.data.ApiConexion.PartidoResponse
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

val FondoOscuro = Color(0xFF141C18)
val TarjetaVerdeOscuro = Color(0xFF1E2B24)
val VerdeAcento = Color(0xFF81C784)
val TextoGris = Color(0xFFA0AAA0)

@Composable
fun DetalleGrupoScreen(
    grupoId: Int,
    viewModel: QuinielaViewModel,
    onVolver: () -> Unit,
    onNavegarPartido: (Int) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(key1 = grupoId) {
        viewModel.cargarDetalleDelGrupo(grupoId)
    }

    val grupo = estado.grupoSeleccionado

    if (grupo == null) {
        Box(modifier = Modifier.fillMaxSize().background(FondoOscuro), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = VerdeAcento)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            IconButton(onClick = onVolver, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }
        item { HeaderGrupo(grupo) }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        item { ClasificacionSeccion() }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        // PASAMOS EL NOMBRE DEL USUARIO PARA MOSTRARLO
        item { ParticipantesSeccion(grupo.participants_count, estado.nombreUsuario) }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        item { ProximosPartidosSeccion(estado.listaProximosPartidos, onNavegarPartido) }
    }
}

@Composable
fun HeaderGrupo(grupo: GrupoResponse) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("GRUPO ACTIVO", color = VerdeAcento, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = VerdeAcento),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("TOP 5%", color = FondoOscuro, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        Text(grupo.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = TarjetaVerdeOscuro),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = VerdeAcento)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Participantes", color = TextoGris, fontSize = 12.sp)
                        Text("${grupo.participants_count}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = TarjetaVerdeOscuro),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = VerdeAcento)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Código", color = TextoGris, fontSize = 12.sp)
                        Text(grupo.invite_code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ClasificacionSeccion() {
    Column {
        Text("Clasificación", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = TarjetaVerdeOscuro),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Aún no hay datos de clasificación disponibles.", color = TextoGris, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ParticipantesSeccion(cantidadParticipantes: Int, miNombre: String) {
    Column {
        Text("Integrantes del Grupo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            items(cantidadParticipantes) { index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFF2E4035))) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.align(Alignment.Center).size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Si es el índice 0, ponemos mi nombre, si no, enumeramos
                    val nombreMostrar = if (index == 0) miNombre else "Usuario ${index + 1}"
                    Text(nombreMostrar, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ProximosPartidosSeccion(partidos: List<PartidoResponse>, onPartidoClick: (Int) -> Unit) {
    Column {
        Text("Próximos Partidos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (partidos.isEmpty()) {
            Text("No hay partidos programados.", color = TextoGris, fontSize = 14.sp)
        } else {
            partidos.forEach { partido ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TarjetaVerdeOscuro),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onPartidoClick(partido.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(partido.phase.uppercase(), color = VerdeAcento, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            val fechaMostrada = if (partido.match_date.contains("T")) {
                                val fecha = partido.match_date.substringBefore("T")
                                val hora = partido.match_date.substringAfter("T").take(5)
                                "$fecha • $hora"
                            } else {
                                partido.match_date.take(16)
                            }

                            Text(fechaMostrada, color = TextoGris, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            Text(partido.home_team, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("  VS  ", color = TextoGris, fontSize = 14.sp)
                            Text(partido.away_team, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                    }
                }
            }
        }
    }


}
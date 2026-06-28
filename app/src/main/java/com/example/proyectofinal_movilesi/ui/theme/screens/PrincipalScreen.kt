package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaState
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    estado: QuinielaState,
    viewModel: QuinielaViewModel,
    onNavegarMisGrupos: () -> Unit,
    onNavegarPartidos: () -> Unit,
) {

    LaunchedEffect(Unit) {
        viewModel.cargarDatosPrincipales()
    }

    val colorFondo = Color(0xFF121212)
    val colorTarjeta = Color(0xFF1B2A22)
    val colorAcento = Color(0xFFA5D6A7)
    val colorTextoSecundario = Color(0xFFAAAAAA)

    Scaffold(
        containerColor = colorFondo,
        bottomBar = {
            NavigationBar(containerColor = colorFondo, contentColor = colorAcento) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorFondo,
                        selectedTextColor = colorAcento,
                        indicatorColor = colorAcento,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "Grupos") },
                    label = { Text("Grupos") },
                    selected = false,
                    onClick = onNavegarMisGrupos
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Partidos") },
                    label = { Text("Partidos") },
                    selected = false,
                    onClick = onNavegarPartidos
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        if (estado.estaCargando && estado.listaDeGrupos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorAcento)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {

                if (estado.modoOffline) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFB71C1C), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin conexión a Internet. Mostrando datos locales.",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(if (estado.modoOffline) 8.dp else 24.dp))
                    Text("Tu Resumen", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Mis Grupos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "VER TODOS",
                            color = colorAcento,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavegarMisGrupos() }
                        )
                    }
                }

                items(estado.listaDeGrupos.take(2)) { grupo ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(grupo.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("${grupo.participants_count} participantes", color = colorTextoSecundario, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .background(colorAcento, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Puntaje: ${grupo.user_score}", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Próximos Partidos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                }

                items(estado.listaProximosPartidos) { partido ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(modifier = Modifier.background(Color(0xFF2E4035), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text(partido.phase.uppercase(), color = colorAcento, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(partido.match_date.take(10), color = colorTextoSecundario, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(partido.home_team.uppercase(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("VS", color = Color.Gray, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                                Text(partido.away_team.uppercase(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }


                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
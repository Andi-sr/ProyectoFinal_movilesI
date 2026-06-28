package com.example.proyectofinal_movilesi.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaState
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisGruposScreen(
    estado: QuinielaState,
    viewModel: QuinielaViewModel,
    onNavegarDetalleGrupo: (Int) -> Unit // Función que recibe el ID
) {
    val colorFondo = Color(0xFF121212)
    val colorTarjeta = Color(0xFF1B2A22)
    val colorAcento = Color(0xFFA5D6A7)
    val colorFondoBotonOscuro = Color(0xFF2E4035)
    val colorTextoSecundario = Color(0xFFAAAAAA)

    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var mostrarDialogoUnirse by remember { mutableStateOf(false) }
    var campoTexto by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(colorFondo)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp)
        ) {
            item {
                Text("Mis Grupos", color = colorAcento, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Administra tus ligas privadas y compite con amigos.", color = colorTextoSecundario, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
            }

            item {
                Button(
                    onClick = { campoTexto = ""; mostrarDialogoUnirse = true },
                    modifier = Modifier.fillMaxWidth().height(72.dp).padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorFondoBotonOscuro),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.GroupAdd, contentDescription = null, tint = colorAcento, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("UNIRME A UN GRUPO", color = colorAcento, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Ingresa un código de invitación", color = colorTextoSecundario, fontSize = 12.sp)
                        }
                    }
                }

                Button(
                    onClick = { campoTexto = ""; mostrarDialogoCrear = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorAcento),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("CREAR GRUPO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("TUS LIGAS ACTIVAS", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("${estado.listaDeGrupos.size} GRUPOS", color = colorAcento, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (estado.listaDeGrupos.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("Aún no te has unido a ningún grupo.", color = colorTextoSecundario)
                    }
                }
            } else {
                items(estado.listaDeGrupos) { grupo ->
                    val contexto = LocalContext.current

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            // AQUÍ CONECTAMOS EL CLICK CON LA NAVEGACIÓN
                            .clickable { onNavegarDetalleGrupo(grupo.id) },
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(grupo.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver detalles", tint = colorAcento, modifier = Modifier.padding(start = 4.dp))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Person, contentDescription = null, tint = colorTextoSecundario, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${grupo.participants_count} participantes", color = colorTextoSecundario, fontSize = 14.sp)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("PUNTAJE", color = colorTextoSecundario, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("${grupo.user_score} pts", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = colorFondoBotonOscuro, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("CÓDIGO DE INVITACIÓN", color = colorTextoSecundario, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(grupo.invite_code, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                }

                                Button(
                                    onClick = {
                                        val intentEnvio = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "¡Únete a mi liga privada '${grupo.name}' en la Quiniela! El código de invitación es: ${grupo.invite_code}")
                                        }
                                        contexto.startActivity(Intent.createChooser(intentEnvio, "Compartir código vía..."))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorFondoBotonOscuro),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = null, tint = colorAcento, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compartir", color = colorAcento, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DIÁLOGOS DE CREAR / UNIRSE ---
        if (mostrarDialogoCrear) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCrear = false },
                title = { Text("Crear Nuevo Grupo", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Introduce el nombre de tu nueva liga de quiniela:", color = colorTextoSecundario, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(value = campoTexto, onValueChange = { campoTexto = it }, placeholder = { Text("Ej. Amigos de la U") }, colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoBotonOscuro, unfocusedContainerColor = colorFondoBotonOscuro, focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    }
                },
                confirmButton = { Button(colors = ButtonDefaults.buttonColors(containerColor = colorAcento), onClick = { if (campoTexto.isNotBlank()) { viewModel.crearNuevoGrupo(campoTexto) { mostrarDialogoCrear = false } } }) { Text("Crear", color = Color.Black, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { mostrarDialogoCrear = false }) { Text("Cancelar", color = colorAcento) } },
                containerColor = Color(0xFF1E1E1E)
            )
        }

        if (mostrarDialogoUnirse) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoUnirse = false },
                title = { Text("Unirme a una Liga", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Ingresa el código de invitación del grupo al que deseas ingresar:", color = colorTextoSecundario, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(value = campoTexto, onValueChange = { campoTexto = it.uppercase() }, placeholder = { Text("Ej. FÚTBOL-2026") }, colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoBotonOscuro, unfocusedContainerColor = colorFondoBotonOscuro, focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    }
                },
                confirmButton = { Button(colors = ButtonDefaults.buttonColors(containerColor = colorAcento), onClick = { if (campoTexto.isNotBlank()) { viewModel.unirseAGrupoPorCodigo(campoTexto) { mostrarDialogoUnirse = false } } }) { Text("Unirme", color = Color.Black, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { mostrarDialogoUnirse = false }) { Text("Cancelar", color = colorAcento) } },
                containerColor = Color(0xFF1E1E1E)
            )
        }
    }
}
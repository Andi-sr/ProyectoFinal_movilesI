package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaState

@Composable
fun PerfilScreen(
    estado: QuinielaState,
    onCerrarSesion: () -> Unit
) {

    val fondo = Color(0xFF121A16)
    val tarjeta = Color(0xFF1B2A22)
    val verde = Color(0xFFA5D6A7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(verde),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = estado.nombreUsuario,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = estado.correoUsuario,
            color = Color.Gray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        TarjetaDato(
            icono = Icons.Default.EmojiEvents,
            titulo = "Puntaje total",
            valor = estado.puntajeTotal.toString(),
            color = tarjeta,
            colorIcono = verde
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaDato(
            icono = Icons.Default.Groups,
            titulo = "Grupos",
            valor = estado.cantidadGrupos.toString(),
            color = tarjeta,
            colorIcono = verde
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaDato(
            icono = Icons.Default.SportsSoccer,
            titulo = "Pronósticos",
            valor = estado.cantidadPronosticos.toString(),
            color = tarjeta,
            colorIcono = verde
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "● Sesión iniciada",
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC62828)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "CERRAR SESIÓN",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

    }

}

@Composable
fun TarjetaDato(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String,
    color: Color,
    colorIcono: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    titulo,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Text(
                    valor,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

            }

        }

    }

}
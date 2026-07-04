package com.example.proyectofinal_movilesi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEstadioScreen(
    onVolver: () -> Unit
) {

    Scaffold(

        containerColor = Color(0xFF121A16),

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Detalle del Estadio",
                        color = Color.White
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onVolver
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )

                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121A16)
                )

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

            Card(

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2A22)
                ),

                shape = RoundedCornerShape(18.dp)

            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "MetLife Stadium",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFA5D6A7)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            "New Jersey, Estados Unidos",
                            color = Color.White
                        )

                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            tint = Color(0xFFA5D6A7)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            "Capacidad: 82 500 espectadores",
                            color = Color.White
                        )

                    }

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                "Partidos programados",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2A22)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        "Brasil vs Argentina",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "15 Julio 2026",
                        color = Color.Gray
                    )

                }

            }

        }

    }

}

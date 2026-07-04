package com.example.proyectofinal_movilesi.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectofinal_movilesi.viewmodel.QuinielaState

@Composable
fun PerfilScreen(
    estado: QuinielaState,
    onCerrarSesion: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Top

    ) {

        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(25.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(estado.nombreUsuario)

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Correo",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(estado.correoUsuario)

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Puntaje acumulado",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("${estado.puntajeTotal}")

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Cantidad de grupos",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("${estado.cantidadGrupos}")

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Cantidad de pronósticos",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("${estado.cantidadPronosticos}")

            }

        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onCerrarSesion
        ) {

            Text("Cerrar sesión")

        }

    }

}
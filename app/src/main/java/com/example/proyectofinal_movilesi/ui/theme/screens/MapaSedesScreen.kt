package com.example.proyectofinal_movilesi.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import androidx.compose.foundation.layout.padding
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaSedesScreen(
    onNavegarDetalleEstadio: () -> Unit
) {

    val estadioInicial = LatLng(40.8296, -73.9262)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(estadioInicial, 4f)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text("Mapa de Sedes")
                }
            )
        }

    ) { paddingValues ->

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
        {

            Marker(
                state = MarkerState(estadioInicial),
                title = "MetLife Stadium",
                snippet = "Final del Mundial"
            )
            Marker(
                state = MarkerState(LatLng(34.1613, -118.1676)),
                title = "Rose Bowl",
                snippet = "Los Ángeles"
            )

            Marker(
                state = MarkerState(LatLng(25.9580, -80.2389)),
                title = "Hard Rock Stadium",
                snippet = "Miami"
            )

            Marker(
                state = MarkerState(LatLng(29.6847, -95.4107)),
                title = "NRG Stadium",
                snippet = "Houston"
            )

            Marker(
                state = MarkerState(LatLng(39.7439, -105.0201)),
                title = "Empower Field",
                snippet = "Denver"
            )

        }

    }

}

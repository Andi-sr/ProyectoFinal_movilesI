package com.example.proyectofinal_movilesi.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyectofinal_movilesi.viewmodel.QuinielaState
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaSedesScreen(
    estado: QuinielaState,
    viewModel: QuinielaViewModel,
    onNavegarDetalleEstadio: (Int) -> Unit
) {

    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    LaunchedEffect(Unit) {
        viewModel.cargarEstadiosYPredicciones()
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val estadioInicial = LatLng(40.8296, -73.9262)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(estadioInicial, 4f)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mapa de Sedes") })
        }
    ) { paddingValues ->

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState, // EL MAPA AHORA SÍ SE CENTRA
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission) // ACTIVA EL GPS
        ) {
            // PUNTO 3: Sedes dibujadas dinámicamente desde la API[cite: 2]
            estado.listaEstadios.forEach { estadio ->
                Marker(
                    state = MarkerState(LatLng(estadio.latitude, estadio.longitude)),
                    title = estadio.name,
                    snippet = "${estadio.city}, ${estadio.country}",
                    onClick = {
                        onNavegarDetalleEstadio(estadio.id)
                        true
                    }
                )
            }
        }
    }
}
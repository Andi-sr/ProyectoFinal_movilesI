package com.example.proyectofinal_movilesi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.proyectofinal_movilesi.data.DataStoreManager
import com.example.proyectofinal_movilesi.data.ApiConexion.RetrofitClient // Importamos tu nuevo cliente
import com.example.proyectofinal_movilesi.data.db.QuinielaDatabase
import com.example.proyectofinal_movilesi.repository.QuinielaRepository
import com.example.proyectofinal_movilesi.ui.theme.ProyectoFinal_MovilesITheme
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinal_MovilesITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val contexto = LocalContext.current

                    // 1. Inicializamos Base de Datos
                    val baseDeDatos = remember {
                        Room.databaseBuilder(
                            contexto,
                            QuinielaDatabase::class.java,
                            "quiniela_database"
                        ).build()
                    }

                    // 2. Inicializamos DataStore
                    val dataStoreManager = remember { DataStoreManager(contexto) }

                    // 3. Creamos Repositorio (Notarás que ahora usamos RetrofitClient.api)
                    val repositorio = remember {
                        QuinielaRepository(
                            api = RetrofitClient.api, // <-- Llamamos a tu archivo de la carpeta data
                            usuarioDao = baseDeDatos.usuarioDao(),
                            grupoDao = baseDeDatos.grupoDao(),
                            partidoDao = baseDeDatos.partidoDao(),
                            dataStoreManager = dataStoreManager
                        )
                    }

                    // 4. Creamos el ViewModel
                    val viewModel: QuinielaViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return QuinielaViewModel(repositorio) as T
                            }
                        }
                    )

                    // 5. Arrancamos la UI
                    SistemaDeNavegacion(viewModel = viewModel)
                }
            }
        }
    }
}
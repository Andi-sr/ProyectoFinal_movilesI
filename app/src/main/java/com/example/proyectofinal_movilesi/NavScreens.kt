package com.example.proyectofinal_movilesi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectofinal_movilesi.screens.DetalleGrupoScreen
import com.example.proyectofinal_movilesi.screens.DetallePartidoScreen
import com.example.proyectofinal_movilesi.screens.LoginScreen
import com.example.proyectofinal_movilesi.screens.MisGruposScreen
import com.example.proyectofinal_movilesi.screens.PartidosScreen
import com.example.proyectofinal_movilesi.screens.PrincipalScreen
import com.example.proyectofinal_movilesi.viewmodel.QuinielaViewModel
import com.example.proyectofinal_movilesi.screens.PerfilScreen
import com.example.proyectofinal_movilesi.screens.MapaSedesScreen
import com.example.proyectofinal_movilesi.screens.DetalleEstadioScreen

@Composable
fun SistemaDeNavegacion(viewModel: QuinielaViewModel) {
    val navController = rememberNavController()
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado.estaAutenticado) {
        if (estado.estaAutenticado && navController.currentDestination?.route == "login") {
            navController.navigate("principal") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                estado = estado,
                onIniciarSesion = { correo, password -> viewModel.iniciarSesion(correo, password) },
                onNavegarPrincipal = {
                    navController.navigate("principal") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("principal") {
            PrincipalScreen(
                estado = estado,
                viewModel = viewModel,
                onNavegarMisGrupos = {
                    navController.navigate("mis_grupos")
                },
                onNavegarPartidos = {
                    navController.navigate("partidos")
                },
                onNavegarMapa = {
                    navController.navigate("mapa_sedes")
                },
                onNavegarPerfil = {
                    navController.navigate("perfil")
                }
            )
        }

        composable("mis_grupos") {
            MisGruposScreen(
                estado = estado,
                viewModel = viewModel,
                onNavegarDetalleGrupo = { grupoId ->
                    navController.navigate("detalle_grupo/$grupoId")
                }
            )
        }

        composable(
            route = "detalle_grupo/{grupoId}",
            arguments = listOf(navArgument("grupoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getInt("grupoId") ?: 0

            DetalleGrupoScreen(
                grupoId = grupoId,
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onNavegarPartido = { partidoId ->
                    navController.navigate("detalle_partido/$partidoId")
                }
            )
        }

        composable("partidos") {
            PartidosScreen(
                estado = estado,
                viewModel = viewModel,
                onNavegarDetallePartido = { partidoId ->
                    navController.navigate("detalle_partido/$partidoId")
                }
            )
        }

        composable(
            route = "detalle_partido/{partidoId}",
            arguments = listOf(
                navArgument("partidoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val partidoId = backStackEntry.arguments?.getInt("partidoId") ?: 0

            DetallePartidoScreen(
                partidoId = partidoId,
                viewModel = viewModel,
                onVolver = {
                    navController.popBackStack()
                },
                onVerEstadio = { estadioId ->
                    // Navegamos pasando el ID del estadio que nos manda la pantalla del partido
                    navController.navigate("detalle_estadio/$estadioId")
                }
            )
        }

        composable("perfil") {
            PerfilScreen(
                estado = estado,
                onCerrarSesion = {
                    viewModel.cerrarSesion {
                        navController.navigate("login") {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable("mapa_sedes") {
            MapaSedesScreen(
                estado = estado,
                viewModel = viewModel,
                onNavegarDetalleEstadio = { estadioId ->
                    navController.navigate("detalle_estadio/$estadioId")
                }
            )
        }


        composable(
            route = "detalle_estadio/{estadioId}",
            arguments = listOf(navArgument("estadioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val estadioId = backStackEntry.arguments?.getInt("estadioId") ?: 0

            DetalleEstadioScreen(
                estadioId = estadioId,
                viewModel = viewModel,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}

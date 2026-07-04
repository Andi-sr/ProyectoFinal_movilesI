package com.example.proyectofinal_movilesi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal_movilesi.repository.QuinielaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuinielaViewModel(private val repositorio: QuinielaRepository) : ViewModel() {

    private val _estado = MutableStateFlow(QuinielaState())
    val estado: StateFlow<QuinielaState> = _estado.asStateFlow()

    // --- NUEVO: Esto se ejecuta apenas se abre la app para recordar la sesión ---
    init {
        verificarSesionGuardada()
        cargarDatosPrincipales()
    }

    private fun verificarSesionGuardada() {
        viewModelScope.launch {
            repositorio.obtenerTokenLocal().collect { tokenGuardado ->
                if (!tokenGuardado.isNullOrBlank()) {
                    _estado.value = _estado.value.copy(
                        estaAutenticado = true,
                        tokenAcceso = tokenGuardado
                    )
                    // Si ya estaba logueado, cargamos sus datos automáticamente
                    cargarDatosPrincipales()
                }
            }
        }
    }

    fun iniciarSesion(correo: String, contrasenia: String) {
        if (correo.isBlank() || contrasenia.isBlank()) {
            _estado.value = _estado.value.copy(mensajeError = "Por favor, llena todos los campos.")
            return
        }

        viewModelScope.launch {
            _estado.value = _estado.value.copy(estaCargando = true, mensajeError = null)

            try {
                val respuesta = repositorio.autenticarUsuario(correo, contrasenia)

                // GUARDA SESIÓN
                repositorio.guardarSesion(respuesta.token, respuesta.name, respuesta.email)

                _estado.value = _estado.value.copy(
                    estaCargando = false,
                    estaAutenticado = true,
                    tokenAcceso = respuesta.token,
                    nombreUsuario = respuesta.name,
                    correoUsuario = respuesta.email
                )
                cargarDatosPrincipales()
            } catch (e: Exception) {
                android.util.Log.e("ERROR_LOGIN", "Falla: ${e.message}", e)
                _estado.value = _estado.value.copy(
                    estaCargando = false,
                    mensajeError = "Credenciales incorrectas o error de conexión."
                )
            }
        }
    }

    fun cargarDatosPrincipales() {
        val token = _estado.value.tokenAcceso
        if (token.isNullOrBlank()) return

        viewModelScope.launch {
            _estado.value = _estado.value.copy(estaCargando = true)
            try {
                // 1. Guarda todo en la base de datos
                repositorio.sincronizarDatosPrincipales(token)

                // 2. Carga los grupos para la pantalla Y EL PERFIL
                cargarMisGrupos(token)
                cargarPerfil()
                // Cargamos los partidos para la pantalla
                val partidos = repositorio.api.obtenerProximosPartidos("Bearer $token")
                _estado.value = _estado.value.copy(
                    listaProximosPartidos = partidos,
                    estaCargando = false
                )
            } catch (e: Exception) {
                android.util.Log.e("ERROR_SYNC", "Error al sincronizar", e)
                _estado.value = _estado.value.copy(estaCargando = false)
            }
        }
    }

    fun cargarMisGrupos(token: String) {
        viewModelScope.launch {
            try {
                val grupos = repositorio.api.obtenerMisGrupos("Bearer $token")
                _estado.value = _estado.value.copy(listaDeGrupos = grupos)
            } catch (e: Exception) {
                android.util.Log.e("ERROR_GRUPOS", "Error al cargar grupos", e)
            }
        }
    }

    fun cargarDetalleDelGrupo(grupoId: Int) {
        viewModelScope.launch {
            val grupoEncontrado = _estado.value.listaDeGrupos.find { it.id == grupoId }
            _estado.value = _estado.value.copy(grupoSeleccionado = grupoEncontrado)
        }
    }

    fun crearNuevoGrupo(nombre: String, onExito: () -> Unit) {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                repositorio.crearGrupo(token, nombre)
                cargarMisGrupos(token)
                onExito()
            } catch (e: Exception) {
                android.util.Log.e("ERROR_CREAR", "Error al crear grupo", e)
            }
        }
    }

    fun unirseAGrupoPorCodigo(codigo: String, onExito: () -> Unit) {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                repositorio.unirseAGrupo(token, codigo)
                cargarMisGrupos(token)
                onExito()
            } catch (e: Exception) {
                android.util.Log.e("ERROR_UNIR", "Error al unirse al grupo", e)
            }
        }
    }


    fun cargarTodosLosPartidos() {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                // Llamada al endpoint GET /matches
                val partidos = repositorio.api.obtenerTodosLosPartidos("Bearer $token")
                _estado.value = _estado.value.copy(listaCompletaPartidos = partidos)
            } catch (e: Exception) {
                android.util.Log.e("ERROR_MATCHES", "No se pudo cargar el calendario", e)
            }
        }
    }
    fun cargarPerfil() {
        val token = _estado.value.tokenAcceso ?: return

        viewModelScope.launch {
            try {
                val perfil = repositorio.obtenerPerfil(token)

                _estado.value = _estado.value.copy(
                    nombreUsuario = perfil.name,
                    correoUsuario = perfil.email,
                    puntajeTotal = perfil.total_score,
                    cantidadGrupos = perfil.groups_count,
                    cantidadPronosticos = perfil.predictions_count
                )

            } catch (e: Exception) {
                android.util.Log.e(
                    "ERROR_PROFILE",
                    "No se pudo obtener el perfil",
                    e
                )
            }
        }
    }
}
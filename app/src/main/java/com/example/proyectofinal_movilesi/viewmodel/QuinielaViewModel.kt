package com.example.proyectofinal_movilesi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal_movilesi.data.GrupoResponse
import com.example.proyectofinal_movilesi.data.PartidoResponse
import com.example.proyectofinal_movilesi.repository.QuinielaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuinielaViewModel(private val repositorio: QuinielaRepository) : ViewModel() {

    private val _estado = MutableStateFlow(QuinielaState())
    val estado: StateFlow<QuinielaState> = _estado.asStateFlow()

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
        val token = _estado.value.tokenAcceso ?: return

        viewModelScope.launch {
            _estado.value = _estado.value.copy(estaCargando = true)
            try {
                // INTENTA SINCRONIZAR CON INTERNET
                repositorio.sincronizarDatosPrincipales(token)

                // Si tiene internet, descarga todo fresco de la API
                cargarMisGrupos(token)
                cargarPerfil()
                val partidos = repositorio.api.obtenerProximosPartidos("Bearer $token")

                _estado.value = _estado.value.copy(
                    listaProximosPartidos = partidos,
                    estaCargando = false,
                    modoOffline = false
                )
            } catch (e: Exception) {
                android.util.Log.e("OFFLINE", "Sin conexión. Activando Local-First", e)

               //modo offline
                val gruposLocal = repositorio.obtenerGruposLocales().map {
                    GrupoResponse(it.id, it.nombre, it.participantesCount, it.userScore, it.inviteCode)
                }

                val partidosLocal = repositorio.obtenerPartidosLocales().map {
                    PartidoResponse(it.id, it.homeTeam, it.awayTeam, it.matchDate, it.phase, it.status)
                }

                val perfilLocal = repositorio.obtenerPerfilLocal()

                _estado.value = _estado.value.copy(
                    estaCargando = false,
                    modoOffline = true,
                    listaDeGrupos = gruposLocal,
                    listaProximosPartidos = partidosLocal.take(5),
                    nombreUsuario = perfilLocal?.nombre ?: _estado.value.nombreUsuario,
                    correoUsuario = perfilLocal?.email ?: _estado.value.correoUsuario,
                    puntajeTotal = perfilLocal?.totalScore ?: _estado.value.puntajeTotal,
                    cantidadGrupos = perfilLocal?.groupsCount ?: _estado.value.cantidadGrupos,
                    cantidadPronosticos = perfilLocal?.predictionsCount ?: _estado.value.cantidadPronosticos
                )
            }
        }
    }


    fun cargarTodosLosPartidos() {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                val partidos = repositorio.api.obtenerTodosLosPartidos("Bearer $token")
                _estado.value = _estado.value.copy(listaCompletaPartidos = partidos, modoOffline = false)
            } catch (e: Exception) {

                val partidosLocal = repositorio.obtenerPartidosLocales().map {
                    PartidoResponse(it.id, it.homeTeam, it.awayTeam, it.matchDate, it.phase, it.status)
                }
                _estado.value = _estado.value.copy(listaCompletaPartidos = partidosLocal, modoOffline = true)
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

    fun crearNuevoGrupo(nombre: String, onExito: () -> Unit, onError: (String) -> Unit) {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                repositorio.crearGrupo(token, nombre)
                val grupos = repositorio.api.obtenerMisGrupos("Bearer $token")
                _estado.value = _estado.value.copy(listaDeGrupos = grupos)
                onExito()
            } catch (e: Exception) {
                onError(e.message ?: "Error al intentar crear el grupo")
            }
        }
    }

    fun unirseAGrupoPorCodigo(codigo: String, onExito: () -> Unit, onError: (String) -> Unit) {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                repositorio.unirseAGrupo(token, codigo)
                val grupos = repositorio.api.obtenerMisGrupos("Bearer $token")
                _estado.value = _estado.value.copy(listaDeGrupos = grupos)
                onExito()
            } catch (e: Exception) {
                onError(e.message ?: "Código inválido o error de conexión")
            }
        }
    }

    fun sincronizarPartidosEnTiempoReal() {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                val actualizaciones = repositorio.api.obtenerActualizacionesPartidos("Bearer $token")
                if (actualizaciones.isNotEmpty()) {
                    val listaActual = _estado.value.listaCompletaPartidos.toMutableList()
                    actualizaciones.forEach { partidoActualizado ->
                        val index = listaActual.indexOfFirst { it.id == partidoActualizado.id }
                        if (index != -1) {
                            listaActual[index] = partidoActualizado
                        } else {
                            listaActual.add(partidoActualizado)
                        }
                    }
                    _estado.value = _estado.value.copy(listaCompletaPartidos = listaActual)
                }
            } catch (e: com.google.gson.JsonSyntaxException) {
                android.util.Log.e("TIEMPO_REAL", "La API devolvió un formato inesperado", e)
            } catch (e: Exception) {
                android.util.Log.e("TIEMPO_REAL", "Fallo de conexión en segundo plano", e)
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
                android.util.Log.e("ERROR_PROFILE", "No se pudo obtener el perfil", e)
            }
        }
    }

    fun cargarEstadiosYPredicciones() {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                val predicciones = repositorio.api.obtenerMisPredicciones("Bearer $token")
                val estadios = repositorio.api.obtenerEstadios("Bearer $token")
                _estado.value = _estado.value.copy(
                    misPredicciones = predicciones,
                    listaEstadios = estadios
                )
            } catch (e: Exception) {
                android.util.Log.e("ERROR_EXTRA", "No se pudieron cargar estadios o predicciones", e)
            }
        }
    }

    fun guardarPronostico(
        partidoId: Int,
        golesLocal: Int,
        golesVisitante: Int,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = _estado.value.tokenAcceso ?: return
        viewModelScope.launch {
            try {
                repositorio.registrarPrediccion(
                    token = token,
                    partidoId = partidoId,
                    golesLocal = golesLocal,
                    golesVisitante = golesVisitante
                )

                val prediccionesActualizadas = repositorio.api.obtenerMisPredicciones("Bearer $token")
                _estado.value = _estado.value.copy(misPredicciones = prediccionesActualizadas)

                onExito()
            } catch (e: Exception) {
                onError(e.message ?: "Error de conexión con la API")
            }
        }
    }

    fun cerrarSesion(onExito: () -> Unit) {
        viewModelScope.launch {
            repositorio.cerrarSesion()
            _estado.value = QuinielaState()
            onExito()
        }
    }
}
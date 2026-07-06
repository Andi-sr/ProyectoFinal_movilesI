package com.example.proyectofinal_movilesi.repository

import com.example.proyectofinal_movilesi.data.DataStoreManager
import com.example.proyectofinal_movilesi.data.GrupoDao

import com.example.proyectofinal_movilesi.data.ApiConexion.LoginRequest
import com.example.proyectofinal_movilesi.data.PartidoDao
import com.example.proyectofinal_movilesi.data.UsuarioDao
import com.example.proyectofinal_movilesi.data.ApiConexion.QuinielaApi
import com.example.proyectofinal_movilesi.data.ApiConexion.CrearGrupoRequest
import com.example.proyectofinal_movilesi.data.ApiConexion.UnirseGrupoRequest
import com.example.proyectofinal_movilesi.data.entities.GrupoEntity
import com.example.proyectofinal_movilesi.data.entities.PartidoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.proyectofinal_movilesi.data.ApiConexion.PerfilResponse
import com.example.proyectofinal_movilesi.data.ApiConexion.PrediccionRequest

class QuinielaRepository(
    val api: QuinielaApi,
    private val grupoDao: GrupoDao,
    private val partidoDao: PartidoDao,
    private val usuarioDao: UsuarioDao,
    private val dataStoreManager: DataStoreManager
) {
    fun obtenerTokenLocal() = dataStoreManager.tokenGuardado

    suspend fun guardarSesion(token: String, nombre: String, correo: String) {
        dataStoreManager.guardarUsuario(token, nombre, correo)
    }

    suspend fun autenticarUsuario(correo: String, contrasenia: String) = api.login(LoginRequest(correo, contrasenia))

    suspend fun sincronizarDatosPrincipales(token: String) = withContext(Dispatchers.IO) {
        val respuestaGrupos = api.obtenerMisGrupos("Bearer $token")
        val respuestaPartidos = api.obtenerProximosPartidos("Bearer $token")

        grupoDao.borrarTodosLosGrupos()
        partidoDao.borrarTodosLosPartidos()

        //saca información de la api y los mapea para guardar en la base de datos local
        val entidadesGrupos = respuestaGrupos.map { grupo ->
            GrupoEntity(
                id = grupo.id,
                nombre = grupo.name,
                participantesCount = grupo.participants_count,
                userScore = grupo.user_score,
                inviteCode = grupo.invite_code
            )
        }
        val entidadesPartidos = respuestaPartidos.map { partido ->
            PartidoEntity(
                id = partido.id,
                homeTeam = partido.home_team,
                awayTeam = partido.away_team,
                matchDate = partido.match_date,
                phase = partido.phase,
                status = partido.status
            )
        }

        //datos  dao listos para guardarlo en el telefono de forma local
        grupoDao.insertarGrupos(entidadesGrupos)
        partidoDao.insertarPartidos(entidadesPartidos)
    }

    suspend fun obtenerPerfilLocal() = withContext(Dispatchers.IO) { usuarioDao.obtenerUsuario() }
    suspend fun obtenerGruposLocales() = withContext(Dispatchers.IO) { grupoDao.obtenerTodosLosGrupos() }
    suspend fun obtenerPartidosLocales() = withContext(Dispatchers.IO) { partidoDao.obtenerTodosLosPartidos() }

    suspend fun crearGrupo(token: String, nombreGrupo: String) = api.crearGrupo("Bearer $token", CrearGrupoRequest(nombreGrupo))
    suspend fun unirseAGrupo(token: String, codigoInvitacion: String) = api.unirseAGrupo("Bearer $token", UnirseGrupoRequest(codigoInvitacion))
    suspend fun obtenerPerfil(token: String): PerfilResponse {
        return api.obtenerPerfil("Bearer $token")
    }

    suspend fun registrarPrediccion(
        token: String,
        partidoId: Int,
        golesLocal: Int,
        golesVisitante: Int
    ) {
        val response = api.registrarPrediccion(
            token = "Bearer $token",
            request = PrediccionRequest(
                match_id = partidoId,
                home_score = golesLocal,
                away_score = golesVisitante
            )
        )

        if (!response.isSuccessful) {
            throw Exception(
                "Error ${response.code()} : ${response.errorBody()?.string()}"
            )
        }
    }
    suspend fun cerrarSesion() {
        dataStoreManager.borrarSesion()
    }
}
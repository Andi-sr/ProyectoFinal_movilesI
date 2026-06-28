package com.example.proyectofinal_movilesi.repository

import com.example.proyectofinal_movilesi.data.DataStoreManager
import com.example.proyectofinal_movilesi.data.GrupoDao

import com.example.proyectofinal_movilesi.data.LoginRequest
import com.example.proyectofinal_movilesi.data.PartidoDao
import com.example.proyectofinal_movilesi.data.UsuarioDao
import com.example.proyectofinal_movilesi.data.QuinielaApi
import com.example.proyectofinal_movilesi.data.CrearGrupoRequest
import com.example.proyectofinal_movilesi.data.UnirseGrupoRequest
import com.example.proyectofinal_movilesi.data.entities.GrupoEntity
import com.example.proyectofinal_movilesi.data.entities.PartidoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuinielaRepository(
    val api: QuinielaApi, // ¡Aquí quitamos el private!
    private val grupoDao: GrupoDao,
    private val partidoDao: PartidoDao,
    private val usuarioDao: UsuarioDao,
    private val dataStoreManager: DataStoreManager
) {
    fun obtenerTokenLocal() = dataStoreManager.tokenGuardado

    suspend fun guardarSesion(token: String, nombre: String, correo: String) {
        dataStoreManager.guardarUsuario(token, nombre, correo)
    }

    suspend fun login(correo: String, contrasenia: String) = api.login(LoginRequest(correo, contrasenia))
    suspend fun autenticarUsuario(correo: String, contrasenia: String) = api.login(LoginRequest(correo, contrasenia))

    suspend fun sincronizarDatosPrincipales(token: String) = withContext(Dispatchers.IO) {
        val respuestaGrupos = api.obtenerMisGrupos("Bearer $token")
        val respuestaPartidos = api.obtenerProximosPartidos("Bearer $token")

        grupoDao.borrarTodosLosGrupos()
        partidoDao.borrarTodosLosPartidos()

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

        grupoDao.insertarGrupos(entidadesGrupos)
        partidoDao.insertarPartidos(entidadesPartidos)
    }

    suspend fun obtenerPerfilLocal() = withContext(Dispatchers.IO) { usuarioDao.obtenerUsuario() }
    suspend fun obtenerGruposLocales() = withContext(Dispatchers.IO) { grupoDao.obtenerTodosLosGrupos() }
    suspend fun obtenerPartidosLocales() = withContext(Dispatchers.IO) { partidoDao.obtenerTodosLosPartidos() }

    suspend fun crearGrupo(token: String, nombreGrupo: String) = api.crearGrupo("Bearer $token", CrearGrupoRequest(nombreGrupo))
    suspend fun unirseAGrupo(token: String, codigoInvitacion: String) = api.unirseAGrupo("Bearer $token", UnirseGrupoRequest(codigoInvitacion))
}
package com.example.proyectofinal_movilesi.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Creamos la instancia del DataStore
private val Context.dataStore by preferencesDataStore(name = "sesion_quiniela")

class DataStoreManager(private val context: Context) {

    // Definimos las "llaves" para guardar cada dato
    companion object {
        val CLAVE_TOKEN = stringPreferencesKey("jwt_token")
        val CLAVE_NOMBRE = stringPreferencesKey("user_name")
        val CLAVE_CORREO = stringPreferencesKey("user_email")
    }

    // Función para guardar los datos al hacer login
    suspend fun guardarUsuario(token: String, nombre: String, correo: String) {
        context.dataStore.edit { preferencias ->
            preferencias[CLAVE_TOKEN] = token
            preferencias[CLAVE_NOMBRE] = nombre
            preferencias[CLAVE_CORREO] = correo
        }
    }

    // Función para leer el token guardado
    val tokenGuardado: Flow<String?> = context.dataStore.data.map { preferencias ->
        preferencias[CLAVE_TOKEN]
    }
    // Función para cerrar sesión
    suspend fun borrarSesion() {
        context.dataStore.edit { preferencias ->
            preferencias.clear()
        }
    }

}
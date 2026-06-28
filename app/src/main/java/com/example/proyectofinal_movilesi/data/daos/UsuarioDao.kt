package com.example.proyectofinal_movilesi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectofinal_movilesi.data.entities.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert
    fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios LIMIT 1")
    fun obtenerUsuario(): UsuarioEntity?

    @Query("DELETE FROM usuarios")
    fun borrarUsuario()
}
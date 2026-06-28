package com.example.proyectofinal_movilesi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectofinal_movilesi.data.entities.GrupoEntity

@Dao
interface GrupoDao {
    @Insert
    fun insertarGrupos(grupos: List<GrupoEntity>)

    @Query("SELECT * FROM grupos")
    fun obtenerTodosLosGrupos(): List<GrupoEntity>

    @Query("DELETE FROM grupos")
    fun borrarTodosLosGrupos()
}
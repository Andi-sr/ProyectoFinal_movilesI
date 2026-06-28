package com.example.proyectofinal_movilesi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectofinal_movilesi.data.entities.PartidoEntity

@Dao
interface PartidoDao {
    @Insert
    fun insertarPartidos(partidos: List<PartidoEntity>)

    @Query("SELECT * FROM partidos")
    fun obtenerTodosLosPartidos(): List<PartidoEntity>

    @Query("DELETE FROM partidos")
    fun borrarTodosLosPartidos()
}
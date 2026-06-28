package com.example.proyectofinal_movilesi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidos")
data class PartidoEntity(
    @PrimaryKey
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val matchDate: String,
    val phase: String,
    val status: String
)
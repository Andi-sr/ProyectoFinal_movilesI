package com.example.proyectofinal_movilesi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val email: String,
    val nombre: String,
    val totalScore: Int,
    val groupsCount: Int,
    val predictionsCount: Int
)
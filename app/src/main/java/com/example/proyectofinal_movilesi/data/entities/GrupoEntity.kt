package com.example.proyectofinal_movilesi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GrupoEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val participantesCount: Int,
    val userScore: Int,
    val inviteCode: String
)
package com.example.proyectofinal_movilesi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal_movilesi.data.GrupoDao
import com.example.proyectofinal_movilesi.data.PartidoDao
import com.example.proyectofinal_movilesi.data.UsuarioDao
import com.example.proyectofinal_movilesi.data.entities.GrupoEntity
import com.example.proyectofinal_movilesi.data.entities.PartidoEntity
import com.example.proyectofinal_movilesi.data.entities.UsuarioEntity


@Database(entities = [GrupoEntity::class, PartidoEntity::class, UsuarioEntity::class], version = 1)
abstract class QuinielaDatabase : RoomDatabase() {
    abstract fun grupoDao(): GrupoDao
    abstract fun partidoDao(): PartidoDao
    abstract fun usuarioDao(): UsuarioDao
}
package com.example.proyectofinal_movilesi.viewmodel

import com.example.proyectofinal_movilesi.data.EstadioResponse
import com.example.proyectofinal_movilesi.data.GrupoResponse
import com.example.proyectofinal_movilesi.data.PartidoResponse
import com.example.proyectofinal_movilesi.data.PrediccionResponse

data class QuinielaState(
    val estaCargando: Boolean = false,
    val estaAutenticado: Boolean = false,
    val tokenAcceso: String? = null,
    val mensajeError: String? = null,

    val modoOffline: Boolean = false,

    val nombreUsuario: String = "",
    val correoUsuario: String = "",
    val puntajeTotal: Int = 0,
    val cantidadGrupos: Int = 0,
    val cantidadPronosticos: Int = 0,


    val listaDeGrupos: List<GrupoResponse> = emptyList(),
    val listaProximosPartidos: List<PartidoResponse> = emptyList(),

    val listaCompletaPartidos: List<PartidoResponse> = emptyList(),
    val listaFiltradaPartidos: List<PartidoResponse> = emptyList(),


    val grupoSeleccionado: GrupoResponse? = null,

    val misPredicciones: List<PrediccionResponse> = emptyList(),
    val listaEstadios: List<EstadioResponse> = emptyList(),
)
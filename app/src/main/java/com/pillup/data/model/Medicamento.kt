package com.pillup.data.model

data class Medicamento(
    val id: String = "",
    val uid: String = "",
    val nombre: String = "",
    val dosis: Int = 0,
    val primeraToma: String = "",
    val intervalo: Int = 0,
    val intervaloUnidad: String = "Horas",
    val duracion: String = "",
    val importancia: String = "",
    val instrucciones: String = "",
    val fotoUrl: String = "",
    val proximaToma: String = ""
)
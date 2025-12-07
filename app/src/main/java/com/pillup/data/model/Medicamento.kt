package com.pillup.data.model

data class Medicamento(
    val id: String = "",
    val uid: String = "",  // Usuario propietario
    val nombre: String = "",
    val dosis: Int = 0,
    val primeraToma: String = "",  // Formato HH:mm
    val intervalo: Int = 0,  // En horas
    val intervaloUnidad: String = "Horas",
    val duracion: String = "",  // Formato "dd/MM/yyyy - dd/MM/yyyy"
    val importancia: String = "",  // "Baja", "Media", "Alta"
    val instrucciones: String = "",
    val fotoUrl: String = "",  // URL de la foto
    val proximaToma: String = ""  // Calculada automáticamente
)
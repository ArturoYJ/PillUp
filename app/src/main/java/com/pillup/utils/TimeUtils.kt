package com.pillup.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeUtils {

    fun calcularProximaToma(primeraToma: String, intervaloHoras: Int): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())

        return try {
            val datePrimeraToma = format.parse(primeraToma) ?: return "Pendiente"

            val calToma = Calendar.getInstance()
            calToma.time = datePrimeraToma

            val calAhora = Calendar.getInstance()

            val calProxima = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, calToma.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, calToma.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            while (calProxima.timeInMillis <= calAhora.timeInMillis) {
                calProxima.add(Calendar.HOUR_OF_DAY, intervaloHoras)
            }

            format.format(calProxima.time)

        } catch (e: Exception) {
            "Error fecha"
        }
    }

    fun sumarHoras(horaBase: String, horasASumar: Int): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val dateBase = format.parse(horaBase) ?: return horaBase
            val calendar = Calendar.getInstance()
            calendar.time = dateBase
            calendar.add(Calendar.HOUR_OF_DAY, horasASumar)
            format.format(calendar.time)
        } catch (e: Exception) {
            horaBase // Si falla, devuelve la original para no romper nada
        }
    }

}
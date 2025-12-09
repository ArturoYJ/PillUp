package com.pillup.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun programarAlarma(context: Context, nombre: String, dosis: String, horaToma: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // 1. Parsear la hora (HH:mm)
            val parts = horaToma.split(":")
            if (parts.size != 2) return

            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            // 2. Configurar el calendario para la alarma
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                // Si la hora ya pasó hoy, programarla para mañana
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            // 3. Crear el Intent para el BroadcastReceiver
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("NOMBRE_MEDICAMENTO", nombre)
                putExtra("DOSIS_MEDICAMENTO", dosis)
            }

            // Usamos el hashCode del nombre como ID único para poder tener múltiples alarmas
            val requestCode = nombre.hashCode()

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 4. Programar la alarma exacta
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.d("PillUpAlarm", "Alarma programada para: ${calendar.time}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
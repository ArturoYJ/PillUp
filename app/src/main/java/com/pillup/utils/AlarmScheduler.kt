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

            val parts = horaToma.split(":")
            if (parts.size != 2) return

            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                // Si la hora ya pasó hoy, programar para mañana
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TIPO_ALERTA", "RECORDATORIO") // Importante para que el Receiver sepa qué hacer
                putExtra("NOMBRE_MEDICAMENTO", nombre)
                putExtra("DOSIS_MEDICAMENTO", dosis)
            }

            // ID único basado en el nombre
            val requestCode = nombre.hashCode()

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.d("PillUpAlarm", "Recordatorio programado para: ${calendar.time}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 2. ALARMA DE EMERGENCIA (SEGURIDAD)
    @SuppressLint("ScheduleExactAlarm")
    fun programarAlarmaEmergencia(context: Context, nombre: String, horaToma: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val parts = horaToma.split(":")
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }

                // TOLERANCIA: 2 minutos para pruebas (cámbialo a 15 para real)
                add(Calendar.MINUTE, 2)
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TIPO_ALERTA", "EMERGENCIA")
                putExtra("NOMBRE_MEDICAMENTO", nombre)
            }

            // ID único DIFERENTE al recordatorio (hashCode + 1)
            val requestCode = nombre.hashCode() + 1

            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.d("PillUpAlarm", "Emergencia programada para: ${calendar.time}")

        } catch (e: Exception) { e.printStackTrace() }
    }

    // 3. CANCELAR ALARMA DE EMERGENCIA
    fun cancelarAlarmaEmergencia(context: Context, nombre: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java)
            val requestCode = nombre.hashCode() + 1 // Debe coincidir con el ID de emergencia

            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d("PillUpAlarm", "Alarma de emergencia cancelada para $nombre")
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}
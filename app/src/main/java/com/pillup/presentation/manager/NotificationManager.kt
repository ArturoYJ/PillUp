package com.pillup.presentation.manager

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pillup.R
import com.pillup.presentation.receiver.MedicamentoAlarmReceiver
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class NotificationManager(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val CHANNEL_ID = "medicamentos_channel"
        const val NOTIFICATION_ID = 1
    }

    // 🔹 PROGRAMAR NOTIFICACIÓN PARA UN MEDICAMENTO
    fun programarNotificacionMedicamento(
        medicamentoId: String,
        nombreMedicamento: String,
        horaToma: String, // Formato HH:mm
        dosis: Int
    ) {
        try {
            val (hora, minuto) = horaToma.split(":").map { it.toInt() }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)

                // Si la hora ya pasó hoy, programar para mañana
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val intent = Intent(context, MedicamentoAlarmReceiver::class.java).apply {
                action = "com.pillup.MEDICAMENTO_NOTIFICATION"
                putExtra("medicamento_id", medicamentoId)
                putExtra("medicamento_nombre", nombreMedicamento)
                putExtra("medicamento_dosis", dosis)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                medicamentoId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔹 CANCELAR NOTIFICACIÓN DE UN MEDICAMENTO
    fun cancelarNotificacionMedicamento(medicamentoId: String) {
        try {
            val intent = Intent(context, MedicamentoAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                medicamentoId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔹 MOSTRAR NOTIFICACIÓN INMEDIATA
    fun mostrarNotificacion(
        titulo: String,
        mensaje: String,
        notificationId: Int = NOTIFICATION_ID
    ) {
        // Verificar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return // No mostrar si no tiene permiso
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // 🔹 MOSTRAR NOTIFICACIÓN CON ACCIONES
    fun mostrarNotificacionConAcciones(
        medicamentoId: String,
        nombreMedicamento: String,
        dosis: Int,
        notificationId: Int = NOTIFICATION_ID
    ) {
        val marcarTomadoIntent = Intent(context, MedicamentoAlarmReceiver::class.java).apply {
            action = "com.pillup.MARCAR_TOMADO"
            putExtra("medicamento_id", medicamentoId)
        }

        val marcarTomadoPendingIntent = PendingIntent.getBroadcast(
            context,
            medicamentoId.hashCode() + 1,
            marcarTomadoIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Toma de medicamento")
            .setContentText("Es hora de tomar $nombreMedicamento ($dosis mg)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Marcar tomado",
                marcarTomadoPendingIntent
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
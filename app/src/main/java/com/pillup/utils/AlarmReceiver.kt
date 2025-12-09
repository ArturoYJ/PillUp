package com.pillup.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.pillup.MainActivity
import com.pillup.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoNombre = intent.getStringExtra("NOMBRE_MEDICAMENTO") ?: "Medicamento"
        val medicamentoDosis = intent.getStringExtra("DOSIS_MEDICAMENTO") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent para abrir la app al tocar la notificación
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, "channel_pillup_alarmas")
            .setSmallIcon(R.mipmap.ic_launcher_round) // Asegúrate que este ícono exista, si no usa R.drawable.ic_launcher_foreground
            .setContentTitle("¡Es hora de tu $medicamentoNombre!")
            .setContentText("Te toca tomar tu dosis de $medicamentoDosis")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación (usamos un ID único basado en el tiempo para que no se sobrescriban)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
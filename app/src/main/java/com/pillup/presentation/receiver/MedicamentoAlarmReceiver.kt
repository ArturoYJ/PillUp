package com.pillup.presentation.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.pillup.presentation.manager.NotificationManager

class MedicamentoAlarmReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            "com.pillup.MEDICAMENTO_NOTIFICATION" -> {
                val medicamentoId = intent.getStringExtra("medicamento_id") ?: return
                val nombreMedicamento = intent.getStringExtra("medicamento_nombre") ?: return
                val dosis = intent.getIntExtra("medicamento_dosis", 0)

                val notificationManager = NotificationManager(context)
                notificationManager.mostrarNotificacionConAcciones(
                    medicamentoId = medicamentoId,
                    nombreMedicamento = nombreMedicamento,
                    dosis = dosis
                )
            }
            "com.pillup.MARCAR_TOMADO" -> {
                val medicamentoId = intent.getStringExtra("medicamento_id") ?: return
                // Aquí puedes agregar lógica para marcar como tomado en la base de datos
                // Por ahora solo mostraremos una notificación de confirmación
                val notificationManager = NotificationManager(context)
                notificationManager.mostrarNotificacion(
                    titulo = "Medicamento registrado",
                    mensaje = "Hemos registrado que tomaste tu medicamento",
                    notificationId = 2
                )
            }
        }
    }
}
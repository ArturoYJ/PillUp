package com.pillup.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.pillup.MainActivity
import com.pillup.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tipoAlerta = intent.getStringExtra("TIPO_ALERTA") ?: "RECORDATORIO"
        val medicamentoNombre = intent.getStringExtra("NOMBRE_MEDICAMENTO") ?: "Medicamento"
        val medicamentoDosis = intent.getStringExtra("DOSIS_MEDICAMENTO") ?: ""

        if (tipoAlerta == "EMERGENCIA") {
            manejarEmergencia(context, medicamentoNombre)
        } else {
            mostrarNotificacion(context, medicamentoNombre, medicamentoDosis)
        }
    }

    private fun mostrarNotificacion(context: Context, nombre: String, dosis: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "channel_pillup_alarmas")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("¡Es hora de tu $nombre!")
            .setContentText("Te toca tomar tu dosis de $dosis")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(nombre.hashCode(), notification)
    }

    private fun manejarEmergencia(context: Context, nombreMedicamento: String) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid

                if (uid != null) {
                    val snapshot = db.collection("users")
                        .document(uid)
                        .collection("emergencia")
                        .document("contacto")
                        .get()
                        .await()

                    val telefono = snapshot.getString("telefono")
                    val contactoNombre = snapshot.getString("nombre") ?: "Contacto"

                    if (!telefono.isNullOrBlank()) {
                        val smsManager = android.telephony.SmsManager.getDefault()
                        val mensaje =
                            "ALERTA PILLUP: El usuario no confirmó la toma de $nombreMedicamento hace 15 min. Por favor verifica su estado."
                        smsManager.sendTextMessage(telefono, null, mensaje, null, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
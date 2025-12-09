package com.pillup.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pillup.data.model.ContactoEmergencia
import kotlinx.coroutines.tasks.await

class ContactoEmergenciaRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun guardarContactoEmergencia(contacto: ContactoEmergencia): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val contactoConUid = contacto.copy(uid = uid)
            db.collection("users")
                .document(uid)
                .collection("emergencia")
                .document("contacto")
                .set(contactoConUid)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerContactoEmergencia(): Result<ContactoEmergencia> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val snapshot = db.collection("users")
                .document(uid)
                .collection("emergencia")
                .document("contacto")
                .get()
                .await()

            val contacto = snapshot.toObject(ContactoEmergencia::class.java)
                ?: throw Exception("Contacto de emergencia no encontrado")

            Result.success(contacto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarContactoEmergencia(): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            db.collection("users")
                .document(uid)
                .collection("emergencia")
                .document("contacto")
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun existeContactoEmergencia(): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val snapshot = db.collection("users")
                .document(uid)
                .collection("emergencia")
                .document("contacto")
                .get()
                .await()

            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
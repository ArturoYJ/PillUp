package com.pillup.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pillup.data.model.Medicamento
import kotlinx.coroutines.tasks.await

class  MedicamentoRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // 🔹 CREAR MEDICAMENTO
    suspend fun crearMedicamento(medicamento: Medicamento): Result<String> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val medConUid = medicamento.copy(uid = uid)
            val docRef = db.collection("users")
                .document(uid)
                .collection("medicamentos")
                .add(medConUid)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 OBTENER MEDICAMENTOS DEL USUARIO
    suspend fun obtenerMedicamentos(): Result<List<Medicamento>> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val snapshot = db.collection("users")
                .document(uid)
                .collection("medicamentos")
                .get()
                .await()

            val medicamentos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Medicamento::class.java)?.copy(id = doc.id)
            }

            Result.success(medicamentos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 OBTENER UN MEDICAMENTO POR ID
    suspend fun obtenerMedicamento(medicamentoId: String): Result<Medicamento> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val snapshot = db.collection("users")
                .document(uid)
                .collection("medicamentos")
                .document(medicamentoId)
                .get()
                .await()

            val medicamento = snapshot.toObject(Medicamento::class.java)
                ?.copy(id = medicamentoId)
                ?: throw Exception("Medicamento no encontrado")

            Result.success(medicamento)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 ACTUALIZAR MEDICAMENTO
    suspend fun actualizarMedicamento(medicamentoId: String, medicamento: Medicamento): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            db.collection("users")
                .document(uid)
                .collection("medicamentos")
                .document(medicamentoId)
                .set(medicamento)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 ELIMINAR MEDICAMENTO
    suspend fun eliminarMedicamento(medicamentoId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            db.collection("users")
                .document(uid)
                .collection("medicamentos")
                .document(medicamentoId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
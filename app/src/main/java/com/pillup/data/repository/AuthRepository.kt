package com.pillup.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pillup.data.model.UserData
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun registerUser(nombre: String, apellidos: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID no encontrado")

            val userData = UserData(
                uid = uid,
                nombre = nombre,
                apellidos = apellidos,
                email = email
            )

            db.collection("users")
                .document(uid)
                .set(userData)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserData> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID no encontrado")

            val snapshot = db.collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(UserData::class.java)
                ?: throw Exception("Usuario no existe en Firestore")

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUser
}
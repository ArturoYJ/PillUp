package com.pillup.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pillup.data.model.Medicamento
import com.pillup.data.repository.MedicamentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.net.Uri

sealed class MedicamentoState {
    object Idle : MedicamentoState()
    object Loading : MedicamentoState()
    data class Success(val medicamentos: List<Medicamento>) : MedicamentoState()
    data class Error(val message: String) : MedicamentoState()
}

sealed class MedicamentoDetailState {
    object Idle : MedicamentoDetailState()
    object Loading : MedicamentoDetailState()
    data class Success(val medicamento: Medicamento) : MedicamentoDetailState()
    data class Error(val message: String) : MedicamentoDetailState()
}

class MedicamentoViewModel(
    private val repo: MedicamentoRepository = MedicamentoRepository()
) : ViewModel() {

    private val _medicamentoState = MutableStateFlow<MedicamentoState>(MedicamentoState.Idle)
    val medicamentoState: StateFlow<MedicamentoState> = _medicamentoState

    private val _medicamentoDetailState = MutableStateFlow<MedicamentoDetailState>(MedicamentoDetailState.Idle)
    val medicamentoDetailState: StateFlow<MedicamentoDetailState> = _medicamentoDetailState

    // 🔹 FUNCIÓN CLAVE: Reinicia el estado para evitar redirecciones automáticas
    fun limpiarEstado() {
        _medicamentoState.value = MedicamentoState.Idle
    }

    fun obtenerMedicamentos() {
        viewModelScope.launch {
            _medicamentoState.value = MedicamentoState.Loading

            val result = repo.obtenerMedicamentos()

            _medicamentoState.value = if (result.isSuccess) {
                MedicamentoState.Success(result.getOrNull() ?: emptyList())
            } else {
                MedicamentoState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun obtenerMedicamento(medicamentoId: String) {
        viewModelScope.launch {
            _medicamentoDetailState.value = MedicamentoDetailState.Loading

            val result = repo.obtenerMedicamento(medicamentoId)

            _medicamentoDetailState.value = if (result.isSuccess) {
                MedicamentoDetailState.Success(result.getOrNull()!!)
            } else {
                MedicamentoDetailState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    // Versión OFFLINE: Solo guarda los datos en Firestore, la foto ya viene como ruta local
    fun crearMedicamento(medicamento: Medicamento) {
        viewModelScope.launch {
            _medicamentoState.value = MedicamentoState.Loading

            try {
                // Ya no subimos nada a Storage.
                // Confiamos en que 'medicamento.fotoUrl' ya tiene la ruta local del archivo.

                val result = repo.crearMedicamento(medicamento)

                if (result.isSuccess) {
                    obtenerMedicamentos() // Recargar lista
                    _medicamentoState.value = MedicamentoState.Success(emptyList())
                } else {
                    throw Exception(result.exceptionOrNull()?.message)
                }

            } catch (e: Exception) {
                _medicamentoState.value = MedicamentoState.Error(e.message ?: "Error desconocido")
            }
        }
    }
    fun actualizarMedicamento(medicamentoId: String, medicamento: Medicamento) {
        viewModelScope.launch {
            _medicamentoDetailState.value = MedicamentoDetailState.Loading

            val result = repo.actualizarMedicamento(medicamentoId, medicamento)

            _medicamentoDetailState.value = if (result.isSuccess) {
                MedicamentoDetailState.Success(medicamento)
            } else {
                MedicamentoDetailState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarMedicamento(medicamentoId: String) {
        viewModelScope.launch {
            val result = repo.eliminarMedicamento(medicamentoId)

            if (result.isSuccess) {
                obtenerMedicamentos()  // Recargar la lista
            }
        }
    }
}
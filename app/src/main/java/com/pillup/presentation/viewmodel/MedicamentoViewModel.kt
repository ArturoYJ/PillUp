package com.pillup.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.pillup.data.model.Medicamento
import com.pillup.data.repository.MedicamentoRepository
import com.pillup.presentation.manager.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
    private val repo: MedicamentoRepository = MedicamentoRepository(),
    private val context: Context? = null
) : ViewModel() {

    private val notificationManager = context?.let { NotificationManager(it) }

    private val _medicamentoState = MutableStateFlow<MedicamentoState>(MedicamentoState.Idle)
    val medicamentoState: StateFlow<MedicamentoState> = _medicamentoState

    private val _medicamentoDetailState = MutableStateFlow<MedicamentoDetailState>(MedicamentoDetailState.Idle)
    val medicamentoDetailState: StateFlow<MedicamentoDetailState> = _medicamentoDetailState

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

    fun crearMedicamento(medicamento: Medicamento) {
        viewModelScope.launch {
            _medicamentoState.value = MedicamentoState.Loading

            val result = repo.crearMedicamento(medicamento)

            _medicamentoState.value = if (result.isSuccess) {
                val medicamentoId = result.getOrNull() ?: ""

                // Programar notificación
                if (medicamento.primeraToma.isNotEmpty()) {
                    notificationManager?.programarNotificacionMedicamento(
                        medicamentoId = medicamentoId,
                        nombreMedicamento = medicamento.nombre,
                        horaToma = medicamento.primeraToma,
                        dosis = medicamento.dosis
                    )
                }

                obtenerMedicamentos()  // Recargar la lista
                MedicamentoState.Success(emptyList())
            } else {
                MedicamentoState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
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
                // Cancelar notificación
                notificationManager?.cancelarNotificacionMedicamento(medicamentoId)

                obtenerMedicamentos()  // Recargar la lista
            }
        }
    }

    fun limpiarEstado() {
        _medicamentoState.value = MedicamentoState.Idle
        _medicamentoDetailState.value = MedicamentoDetailState.Idle
    }
}
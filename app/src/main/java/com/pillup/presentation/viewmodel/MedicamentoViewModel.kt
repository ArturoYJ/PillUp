package com.pillup.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pillup.data.model.Medicamento
import com.pillup.data.repository.MedicamentoRepository
import com.pillup.utils.AlarmScheduler
import com.pillup.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    fun limpiarEstado() {
        _medicamentoState.value = MedicamentoState.Idle
    }
    private fun ordenarPorProximaToma(lista: List<Medicamento>): List<Medicamento> {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val ahora = Calendar.getInstance()
        val horaActualStr = sdf.format(ahora.time)
        val (hoy, manana) = lista.partition { it.proximaToma >= horaActualStr }
        return hoy.sortedBy { it.proximaToma } + manana.sortedBy { it.proximaToma }
    }

    fun obtenerMedicamentos() {
        viewModelScope.launch {
            _medicamentoState.value = MedicamentoState.Loading

            val result = repo.obtenerMedicamentos()

            _medicamentoState.value = if (result.isSuccess) {
                val listaCruda = result.getOrNull() ?: emptyList()
                val listaOrdenada = ordenarPorProximaToma(listaCruda)
                MedicamentoState.Success(listaOrdenada)
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
            try {
                val result = repo.crearMedicamento(medicamento)
                if (result.isSuccess) {
                    obtenerMedicamentos()
                    _medicamentoState.value = MedicamentoState.Success(emptyList()) // Estado temporal para navegar
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
                obtenerMedicamentos()
            }
        }
    }

    fun marcarComoTomado(context: Context, medicamento: Medicamento) {
        viewModelScope.launch {
            val nuevaHora = TimeUtils.sumarHoras(medicamento.proximaToma, medicamento.intervalo)
            val medicamentoActualizado = medicamento.copy(proximaToma = nuevaHora)

            val result = repo.actualizarMedicamento(medicamento.id, medicamentoActualizado)

            if (result.isSuccess) {
                AlarmScheduler.cancelarAlarmaEmergencia(context, medicamento.nombre)

                AlarmScheduler.programarAlarma(
                    context,
                    medicamentoActualizado.nombre,
                    medicamentoActualizado.dosis.toString(),
                    nuevaHora
                )
                AlarmScheduler.programarAlarmaEmergencia(
                    context,
                    medicamentoActualizado.nombre,
                    nuevaHora
                )

                obtenerMedicamento(medicamento.id)
                obtenerMedicamentos()
            }
        }
    }
}
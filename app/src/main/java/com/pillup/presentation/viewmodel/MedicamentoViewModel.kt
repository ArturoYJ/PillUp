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

    // 🔹 NUEVA FUNCIÓN: Marcar como tomado y reprogramar alarmas
    fun marcarComoTomado(context: Context, medicamento: Medicamento) {
        viewModelScope.launch {
            // 1. Calcular la NUEVA hora de toma sumando el intervalo
            val nuevaHora = TimeUtils.sumarHoras(medicamento.proximaToma, medicamento.intervalo)

            // 2. Crear objeto actualizado con la nueva hora
            val medicamentoActualizado = medicamento.copy(
                proximaToma = nuevaHora
            )

            // 3. Actualizar en Firestore
            val result = repo.actualizarMedicamento(medicamento.id, medicamentoActualizado)

            if (result.isSuccess) {
                // 4. Cancelar la ALERTA DE EMERGENCIA de la dosis que acabamos de tomar
                AlarmScheduler.cancelarAlarmaEmergencia(context, medicamento.nombre)

                // 5. Programar el RECORDATORIO de la SIGUIENTE dosis
                AlarmScheduler.programarAlarma(
                    context,
                    medicamentoActualizado.nombre,
                    medicamentoActualizado.dosis.toString(),
                    nuevaHora
                )

                // 6. Programar la ALERTA DE EMERGENCIA de la SIGUIENTE dosis (para seguridad futura)
                AlarmScheduler.programarAlarmaEmergencia(
                    context,
                    medicamentoActualizado.nombre,
                    nuevaHora
                )

                // 7. Refrescar la vista de detalle y la lista general
                obtenerMedicamento(medicamento.id)
                obtenerMedicamentos()
            } else {
                // Opcional: Manejar error de conexión o base de datos
            }
        }
    }
}
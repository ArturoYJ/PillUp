package com.pillup.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pillup.data.model.ContactoEmergencia
import com.pillup.data.repository.ContactoEmergenciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ContactoEmergenciaState {
    object Idle : ContactoEmergenciaState()
    object Loading : ContactoEmergenciaState()
    data class Success(val contacto: ContactoEmergencia) : ContactoEmergenciaState()
    data class Error(val message: String) : ContactoEmergenciaState()
    object NoExiste : ContactoEmergenciaState()
}

class ContactoEmergenciaViewModel(
    private val repo: ContactoEmergenciaRepository = ContactoEmergenciaRepository()
) : ViewModel() {

    private val _contactoState = MutableStateFlow<ContactoEmergenciaState>(ContactoEmergenciaState.Idle)
    val contactoState: StateFlow<ContactoEmergenciaState> = _contactoState

    private val _contactoExiste = MutableStateFlow(false)
    val contactoExiste: StateFlow<Boolean> = _contactoExiste

    fun obtenerContactoEmergencia() {
        viewModelScope.launch {
            _contactoState.value = ContactoEmergenciaState.Loading

            val result = repo.obtenerContactoEmergencia()

            _contactoState.value = if (result.isSuccess) {
                ContactoEmergenciaState.Success(result.getOrNull()!!)
            } else {
                ContactoEmergenciaState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun verificarContactoExiste() {
        viewModelScope.launch {
            val result = repo.existeContactoEmergencia()

            _contactoExiste.value = if (result.isSuccess) {
                result.getOrNull() ?: false
            } else {
                false
            }
        }
    }

    fun guardarContactoEmergencia(contacto: ContactoEmergencia) {
        viewModelScope.launch {
            _contactoState.value = ContactoEmergenciaState.Loading

            val result = repo.guardarContactoEmergencia(contacto)

            _contactoState.value = if (result.isSuccess) {
                _contactoExiste.value = true
                ContactoEmergenciaState.Success(contacto)
            } else {
                ContactoEmergenciaState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarContactoEmergencia() {
        viewModelScope.launch {
            val result = repo.eliminarContactoEmergencia()

            if (result.isSuccess) {
                _contactoExiste.value = false
                _contactoState.value = ContactoEmergenciaState.NoExiste
            }
        }
    }
}
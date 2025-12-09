package com.pillup.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pillup.data.model.UserData
import com.pillup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser

    fun cargarUsuarioActual() {
        val uid = repo.getCurrentUser()?.uid

        if (uid != null) {
            viewModelScope.launch {
                val result = repo.obtenerDatosUsuario(uid)

                if (result.isSuccess) {
                    _currentUser.value = result.getOrNull()
                }
            }
        }
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = repo.loginUser(email, password)

            _loginState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                LoginState.Success(user)
            } else {
                LoginState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserData) : LoginState()
    data class Error(val message: String) : LoginState()
}
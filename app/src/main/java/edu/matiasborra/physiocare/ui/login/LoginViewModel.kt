// File: app/src/main/java/edu/matiasborra/physiocare/ui/login/LoginViewModel.kt
package edu.matiasborra.physiocare.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.auth.LoginState
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: PhysioRepository, private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginState.Error("Usuario y contraseña obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                val resp = repo.login(username, password)
                if (resp.ok) {
                    val token = resp.token ?: ""
                    val userId = resp.userId ?: ""
                    val rol = resp.rol ?: ""
                    val username = resp.login ?: ""
                    session.saveSession(token, username, userId, rol)
                    _uiState.value = LoginState.Success(resp)
                } else {
                    _uiState.value = LoginState.Error(resp.error ?: "Login fallido")
                }
            } catch (e: Exception) {
                _uiState.value = LoginState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }
}

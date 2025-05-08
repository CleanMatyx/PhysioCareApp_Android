// File: app/src/main/java/edu/matiasborra/physiocare/ui/login/LoginViewModel.kt
package edu.matiasborra.physiocare.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle    : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Usuario y contraseña obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val resp = repo.login(username, password)
                if (resp.ok) {
                    val token = resp.token ?: ""
                    val userId = resp.userId ?: ""
                    val rol = resp.rol ?: ""
                    val username = resp.login ?: ""
                    session.saveSession(token, username, userId, rol)
                    _uiState.value = LoginUiState.Success
                    Log.d("LoginViewModel", "Login exitoso")
                    Log.d("LoginViewModel", "Token: $token")
                    Log.d("LoginViewModel", "UserId: $userId")
                    Log.d("LoginViewModel", "Rol: $rol")
                    Log.d("LoginViewModel", "Username: $username")
                } else {
                    _uiState.value = LoginUiState.Error(resp.error ?: "Login fallido")
                    Log.d("LoginViewModel", "Login fallido")
                    Log.d("LoginViewModel", "Error: ${resp.error}")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }
}

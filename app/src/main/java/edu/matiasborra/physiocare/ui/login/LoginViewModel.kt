package edu.matiasborra.physiocare.ui.login

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
                if (resp.ok && resp.result != null) {
                    val token = resp.result.token
                    // Decodificamos el JWT para extraer id y rol
                    val jwt    = JWT(token)
                    val userId = jwt.getClaim("id").asString().orEmpty()
                    val role   = jwt.getClaim("role").asString().orEmpty()
                    // Guardamos token, username, userId y role
                    session.saveSession(token, username, userId, role)
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error(resp.message ?: "Login fallido")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }
}
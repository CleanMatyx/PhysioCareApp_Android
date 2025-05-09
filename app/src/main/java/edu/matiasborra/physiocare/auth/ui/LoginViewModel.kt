package edu.matiasborra.physiocare.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.auth.data.LoginState
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de autenticación en la pantalla de login.
 *
 * @property repo Repositorio para realizar las operaciones de login.
 * @property session Administrador de sesión para guardar los datos del usuario autenticado.
 * @author Matias Borra
 */
class LoginViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    /**
     * Realiza el proceso de login con las credenciales proporcionadas.
     *
     * @param username Nombre de usuario ingresado.
     * @param password Contraseña ingresada.
     */
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

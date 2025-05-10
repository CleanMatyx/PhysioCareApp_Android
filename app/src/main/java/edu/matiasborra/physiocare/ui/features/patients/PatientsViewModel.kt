package edu.matiasborra.physiocare.ui.features.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.data.models.PatientItem
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import edu.matiasborra.physiocare.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Representa los diferentes estados de la interfaz de usuario para la lista de pacientes.
 */
sealed class PatientsUiState {
    /**
     * Estado de carga mientras se obtienen los datos.
     */
    object Loading : PatientsUiState()

    /**
     * Estado de éxito con la lista de pacientes cargada.
     *
     * @param patients Lista de pacientes obtenida.
     */
    data class Success(val patients: List<PatientItem>) : PatientsUiState()

    /**
     * Estado de error con un mensaje descriptivo.
     *
     * @param message Mensaje de error.
     */
    data class Error(val message: String) : PatientsUiState()
}

/**
 * ViewModel para manejar la lógica de negocio relacionada con la lista de pacientes.
 *
 * @param repo Repositorio para acceder a los datos de los pacientes.
 * @param session Administrador de sesión para obtener el token de autenticación.
 */
class PatientsViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager,
    // private val context: Context
) : ViewModel() {

    /**
     * Flujo mutable que representa el estado de la interfaz de usuario.
     */
    private val _uiState = MutableStateFlow<PatientsUiState>(PatientsUiState.Loading)

    /**
     * Flujo inmutable para observar el estado de la interfaz de usuario.
     */
    val uiState: StateFlow<PatientsUiState> = _uiState

    /**
     * Carga la lista de pacientes desde el repositorio.
     * Si el usuario es un paciente, carga solo su perfil.
     */
    fun loadPatients() {
        viewModelScope.launch {
            _uiState.value = PatientsUiState.Loading
            val sd = session.sessionFlow.firstOrNull()
            val token = sd?.token.orEmpty()
            val role = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

            try {
                val list = if (role == "patient") {
                    val single = repo.getPatient(token, userId)
                    if (single.ok && single.result != null) listOf(single.result)
                    else throw Exception(single.message ?: "Error al cargar perfil")
                    // else throw Exception(single.message ?: context.getString(R.string.error_loading_profile))

                } else {
                    val all = repo.getPatients(token)
                    if (all.ok && all.result != null) all.result
                    else throw Exception(all.message ?: "Error al cargar pacientes")
                    // else throw Exception(all.message ?: context.getString(R.string.error_loading_patients))
                }
                _uiState.value = PatientsUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = PatientsUiState.Error(e.localizedMessage ?: "Error inesperado")
                // _uiState.value = PatientsUiState.Error(e.localizedMessage ?: getString(R.string.unexpected_error))
            }
        }
    }
}
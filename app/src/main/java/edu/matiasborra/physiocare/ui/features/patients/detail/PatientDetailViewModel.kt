package edu.matiasborra.physiocare.ui.features.patients.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.data.models.PatientDetailResponse
import edu.matiasborra.physiocare.data.models.PatientItem
import edu.matiasborra.physiocare.data.models.RecordItem
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Representa los diferentes estados de la interfaz de usuario para los detalles del paciente.
 */
sealed class PatientDetailUiState {
    /**
     * Estado de carga mientras se obtienen los datos.
     */
    object Loading : PatientDetailUiState()

    /**
     * Estado de éxito con los datos del paciente y sus registros.
     *
     * @param patient Información del paciente.
     * @param records Lista de registros asociados al paciente.
     */
    data class Success(
        val patient: PatientItem,
        val records: List<RecordItem>
    ) : PatientDetailUiState()

    /**
     * Estado de error con un mensaje descriptivo.
     *
     * @param message Mensaje de error.
     */
    data class Error(val message: String) : PatientDetailUiState()
}

/**
 * ViewModel para manejar la lógica de negocio relacionada con los detalles del paciente.
 *
 * @param repo Repositorio para acceder a los datos del paciente.
 * @param session Administrador de sesión para obtener el token de autenticación.
 * @param patientId ID del paciente cuyos detalles se cargarán.
 */
class PatientDetailViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager,
    private val patientId: String
) : ViewModel() {

    /**
     * Flujo mutable que representa el estado de la interfaz de usuario.
     */
    private val _uiState =
        MutableStateFlow<PatientDetailUiState>(PatientDetailUiState.Loading)

    /**
     * Flujo inmutable para observar el estado de la interfaz de usuario.
     */
    val uiState: StateFlow<PatientDetailUiState> = _uiState

    /**
     * Carga los detalles del paciente y sus registros desde el repositorio.
     */
    fun loadPatientAndRecords() {
        viewModelScope.launch {
            _uiState.value = PatientDetailUiState.Loading
            val token = session.getToken.firstOrNull().orEmpty()
            try {
                val detailResp = repo.getPatientDetail(token, patientId)
                if (detailResp.ok && detailResp.result != null) {
                    val result: PatientDetailResponse = detailResp.result
                    _uiState.value = PatientDetailUiState.Success(
                        patient = result.patient,
                        records = result.records
                    )
                } else {
                    throw Exception(detailResp.message ?: "Error cargando detalle")
                }
            } catch (e: Exception) {
                _uiState.value =
                    PatientDetailUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }

    /**
     * Fábrica para crear instancias de `PatientDetailViewModel`.
     *
     * @param repo Repositorio para acceder a los datos del paciente.
     * @param session Administrador de sesión para obtener el token de autenticación.
     * @param patientId ID del paciente cuyos detalles se cargarán.
     */
    class Factory(private val repo: PhysioRepository, private val session: SessionManager,
                  private val patientId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PatientDetailViewModel(repo, session, patientId) as T
        }
    }
}
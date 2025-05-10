package edu.matiasborra.physiocare.ui.features.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.data.models.AppointmentFlat
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Representa los diferentes estados de la interfaz de usuario para las consultas.
 */
sealed class ConsultationsUiState {

    /**
     * Estado que indica que los datos están cargando.
     */
    object Loading : ConsultationsUiState()

    /**
     * Estado que contiene las consultas de un fisioterapeuta.
     *
     * @property all Lista de todas las consultas.
     */
    data class SuccessPhysio(val all: List<AppointmentFlat>) : ConsultationsUiState()

    /**
     * Estado que contiene las consultas de un paciente.
     *
     * @property pending Lista de consultas pendientes.
     * @property history Lista de consultas pasadas.
     */
    data class SuccessPatient(
        val pending: List<AppointmentFlat>,
        val history: List<AppointmentFlat>
    ) : ConsultationsUiState()

    /**
     * Estado que indica un error al cargar las consultas.
     *
     * @property message Mensaje de error.
     */
    data class Error(val message: String) : ConsultationsUiState()
}

/**
 * ViewModel para manejar la lógica de negocio de las consultas.
 *
 * @property repo Repositorio para acceder a los datos de las consultas.
 * @property session Administrador de sesión para obtener información del usuario.
 */
class ConsultationsViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager
) : ViewModel() {

    /**
     * Flujo de estado de la interfaz de usuario.
     */
    private val _uiState = MutableStateFlow<ConsultationsUiState>(ConsultationsUiState.Loading)
    val uiState: StateFlow<ConsultationsUiState> = _uiState

    /**
     * Carga las consultas del usuario actual según su rol.
     */
    fun loadConsultations() {
        viewModelScope.launch {
            _uiState.value = ConsultationsUiState.Loading

            val sd = session.sessionFlow.firstOrNull()
            val token = sd?.token.orEmpty()
            val role = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

            try {
                if (role == "patient") {
                    val resp = repo.getMyAppointments(token, userId)
                    if (!resp.ok || resp.result == null) {
                        throw Exception(resp.message ?: "No tienes citas programadas")
                    }
                    val apps = resp.result
                    val now = ZonedDateTime.now()
                    val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    val pending = apps.filter { ZonedDateTime.parse(it.date, fmt).isAfter(now) }
                    val history = apps.filter { !ZonedDateTime.parse(it.date, fmt).isAfter(now) }
                    _uiState.value = ConsultationsUiState.SuccessPatient(pending, history)
                } else {
                    val resp = repo.getMyAppointmentsAsPhysio()
                    if (!resp.ok || resp.result == null) {
                        throw Exception(resp.message ?: "Error al cargar citas")
                    }
                    _uiState.value = ConsultationsUiState.SuccessPhysio(resp.result)
                }
            } catch (e: Exception) {
                _uiState.value = ConsultationsUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}
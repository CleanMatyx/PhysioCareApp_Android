package edu.matiasborra.physiocare.ui.main.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.remote.models.AppointmentFlat
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

sealed class ConsultationsUiState {
    object Loading : ConsultationsUiState()
    data class SuccessPhysio(val all: List<AppointmentFlat>) : ConsultationsUiState()
    data class SuccessPatient(
        val pending: List<AppointmentFlat>,
        val history: List<AppointmentFlat>
    ) : ConsultationsUiState()
    data class Error(val message: String) : ConsultationsUiState()
}

class ConsultationsViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConsultationsUiState>(ConsultationsUiState.Loading)
    val uiState: StateFlow<ConsultationsUiState> = _uiState

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

//    fun loadConsultations() {
//        viewModelScope.launch {
//            _uiState.value = ConsultationsUiState.Loading
//            val result = repo.getMyAppointmentsAsPhysio()
//            if (result.ok && result.result != null) {
//                _uiState.value = ConsultationsUiState.SuccessPhysio(result.result)
//            } else {
//                _uiState.value = ConsultationsUiState.Error(result.message ?: "Error cargando citas")
//            }
//        }
//    }
}

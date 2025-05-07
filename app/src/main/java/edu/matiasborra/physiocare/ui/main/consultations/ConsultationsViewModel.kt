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

sealed class ConsultationsUiState {
    object Loading : ConsultationsUiState()
    data class Success(val consultations: List<AppointmentFlat>) : ConsultationsUiState()
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

            val sd      = session.sessionFlow.firstOrNull()
            val token   = sd?.token.orEmpty()
            val username= sd?.username.orEmpty()
            val role    = sd?.role.orEmpty()

            try {
                val resp = repo.getAppointments(token)
                if (resp.ok && resp.result != null) {
                    val all = resp.result
                    // si es patient filtramos solo sus consultas
                    val list = if (role == "patient") {
                        all.filter { it.patientName == username }
                    } else {
                        all
                    }
                    _uiState.value = ConsultationsUiState.Success(list)
                } else {
                    throw Exception(resp.message ?: "Error al cargar consultas")
                }
            } catch (e: Exception) {
                _uiState.value = ConsultationsUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}

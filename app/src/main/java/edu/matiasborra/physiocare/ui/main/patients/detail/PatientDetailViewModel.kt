// File: app/src/main/java/edu/matiasborra/physiocare/ui/main/patients/detail/PatientDetailViewModel.kt
package edu.matiasborra.physiocare.ui.main.patients.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.remote.models.AppointmentItem
import edu.matiasborra.physiocare.data.remote.models.PatientItem
import edu.matiasborra.physiocare.data.remote.models.PatientDetailResponse
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class PatientDetailUiState {
    object Loading : PatientDetailUiState()
    data class Success(
        val patient: PatientItem,
        val medicalRecord: String?,
        val appointments: List<AppointmentItem>
    ) : PatientDetailUiState()
    data class Error(val message: String) : PatientDetailUiState()
}

class PatientDetailViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager,
    private val patientId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientDetailUiState>(PatientDetailUiState.Loading)
    val uiState: StateFlow<PatientDetailUiState> = _uiState

    fun loadPatientAndRecords() {
        viewModelScope.launch {
            _uiState.value = PatientDetailUiState.Loading
            val sd    = session.sessionFlow.firstOrNull()
            val token = sd?.token.orEmpty()
            try {
                val resp = repo.getPatientDetail("Bearer $token", patientId)
                if (!resp.ok || resp.result == null) {
                    throw Exception(resp.message ?: "Error al cargar datos de paciente")
                }
                val pd: PatientDetailResponse = resp.result
                // aplanamos todas las citas de todos los registros
                val allAppts = pd.records
                    .flatMap { it.appointments }
                // opcional: ordenar por fecha, etc.
                // usamos el primer record solo para el campo medicalRecord
                val recordText = pd.records.firstOrNull()?.medicalRecord
                _uiState.value = PatientDetailUiState.Success(
                    patient       = pd.patient,
                    medicalRecord = recordText,
                    appointments  = allAppts
                )
            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }

    class Factory(
        private val repo: PhysioRepository,
        private val session: SessionManager,
        private val patientId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PatientDetailViewModel(repo, session, patientId) as T
        }
    }
}

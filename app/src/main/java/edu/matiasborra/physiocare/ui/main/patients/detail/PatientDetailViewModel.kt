package edu.matiasborra.physiocare.ui.main.patients.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.remote.models.PatientItem
import edu.matiasborra.physiocare.data.remote.models.RecordItem
import edu.matiasborra.physiocare.data.remote.models.AppointmentItem
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class PatientDetailUiState {
    object Loading : PatientDetailUiState()
    data class Success(
        val patient: PatientItem,
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

            val token = session.getToken.firstOrNull().orEmpty()
            try {
                // Traigo el record completo
                val recordResp = repo.getRecord(token, patientId)

                if (recordResp.ok && recordResp.result != null) {
                    val record: RecordItem = recordResp.result
                    val apps: List<AppointmentItem> = record.appointments

                    val patResp = repo.getPatient(token, patientId)
                    if (patResp.ok && patResp.result != null) {
                        _uiState.value = PatientDetailUiState.Success(patResp.result, apps)
                    } else {
                        throw Exception(patResp.message ?: "No se pudo cargar datos del paciente")
                    }
                } else {
                    throw Exception(recordResp.message ?: "Error al cargar record")
                }
            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }

    class Factory(
        private val app: PhysioApp,
        private val patientId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PatientDetailViewModel(
                repo      = PhysioRepository(RemoteDataSource()),
                session   = app.sessionManager,
                patientId = patientId
            ) as T
        }
    }
}
// File: PatientDetailViewModel.kt
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

sealed class PatientDetailUiState {
    object Loading : PatientDetailUiState()
    data class Success(
        val patient: PatientItem,
        val records: List<RecordItem>
    ) : PatientDetailUiState()
    data class Error(val message: String) : PatientDetailUiState()
}

class PatientDetailViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager,
    private val patientId: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PatientDetailUiState>(PatientDetailUiState.Loading)
    val uiState: StateFlow<PatientDetailUiState> = _uiState

    fun loadPatientAndRecords() {
        viewModelScope.launch {
            _uiState.value = PatientDetailUiState.Loading
            val token = session.getToken.firstOrNull().orEmpty()
            try {
                // aquí uso tu nuevo endpoint /patients/{id}
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

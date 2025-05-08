package edu.matiasborra.physiocare.ui.main.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.remote.models.PatientItem
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class PatientsUiState {
    object Loading : PatientsUiState()
    data class Success(val patients: List<PatientItem>) : PatientsUiState()
    data class Error(val message: String) : PatientsUiState()
}

class PatientsViewModel(
    private val repo: PhysioRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientsUiState>(PatientsUiState.Loading)
    val uiState: StateFlow<PatientsUiState> = _uiState

    fun loadPatients() {
        viewModelScope.launch {
            _uiState.value = PatientsUiState.Loading
            val sd     = session.sessionFlow.firstOrNull()
            val token  = sd?.token.orEmpty()
            val role   = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

            try {
                val list = if (role == "patient") {
                    val single = repo.getPatient(token, userId)
                    if (single.ok && single.result != null) listOf(single.result)
                    else throw Exception(single.message ?: "Error al cargar perfil")
                } else {
                    val all = repo.getPatients(token)
                    if (all.ok && all.result != null) all.result
                    else throw Exception(all.message ?: "Error al cargar pacientes")
                }
                _uiState.value = PatientsUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = PatientsUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}

package edu.matiasborra.physiocare.ui.main.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.matiasborra.physiocare.PhysioApp

class ConsultationsViewModelFactory(
    private val app: PhysioApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = app.physioRepo
        val session = app.sessionManager
        return ConsultationsViewModel(repo, session) as T
    }
}

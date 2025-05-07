package edu.matiasborra.physiocare.ui.main.consultations

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import edu.matiasborra.physiocare.databinding.FragmentConsultationsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ConsultationsFragment : Fragment(R.layout.fragment_consultations) {

    private var _binding: FragmentConsultationsBinding? = null
    private val binding get() = _binding!!
    private val app by lazy { requireActivity().application as PhysioApp }
    private val viewModel by viewModels<ConsultationsViewModel> {
        ConsultationsViewModelFactory(app)
    }
    private lateinit var adapter: ConsultationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentConsultationsBinding.bind(view)

        // 1) RecyclerView + adapter
        adapter = ConsultationAdapter()
        binding.rvConsultations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ConsultationsFragment.adapter
        }

        // 2) Observamos UI state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConsultationsUiState.Loading -> {
                        binding.tvLoadingConsultations.isVisible = true
                        binding.rvConsultations.isVisible = false
                    }
                    is ConsultationsUiState.Success -> {
                        binding.tvLoadingConsultations.isVisible = false
                        binding.rvConsultations.isVisible = true
                        adapter.submitList(state.consultations)
                    }
                    is ConsultationsUiState.Error -> {
                        binding.tvLoadingConsultations.text = state.message
                        binding.tvLoadingConsultations.isVisible = true
                        binding.rvConsultations.isVisible = false
                    }
                }
            }
        }

        // 3) Disparar carga
        viewModel.loadConsultations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Suppress("UNCHECKED_CAST")
class ConsultationsViewModelFactory(app: PhysioApp) : ViewModelProvider.Factory {
    private val repo    = PhysioRepository(RemoteDataSource())
    private val session = app.sessionManager

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConsultationsViewModel::class.java)) {
            return ConsultationsViewModel(repo, session) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}

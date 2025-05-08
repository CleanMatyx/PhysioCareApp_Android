// File: app/src/main/java/edu/matiasborra/physiocare/ui/main/consultations/ConsultationsFragment.kt
package edu.matiasborra.physiocare.ui.main.consultations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import edu.matiasborra.physiocare.databinding.FragmentConsultationsBinding
import kotlinx.coroutines.launch

class ConsultationsFragment : Fragment() {
    private var _binding: FragmentConsultationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ConsultationsViewModel> {
        ConsultationsViewModelFactory(requireActivity().application as PhysioApp)
    }

    private lateinit var physioAdapter  : ConsultationAdapter
    private lateinit var pendingAdapter : AppointmentItemAdapter
    private lateinit var historyAdapter : AppointmentItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultationsBinding.inflate(inflater, container, false)

        physioAdapter  = ConsultationAdapter()
        pendingAdapter = AppointmentItemAdapter()
        historyAdapter = AppointmentItemAdapter()

        binding.rvAllConsultations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = physioAdapter
        }
        binding.rvPending.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pendingAdapter
        }
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                //  Reset UI
                binding.apply {
                    rvAllConsultations.isVisible    = false
                    tvPendingTitle.isVisible         = false
                    rvPending.isVisible              = false
                    tvHistoryTitle.isVisible         = false
                    rvHistory.isVisible              = false
                    tvLoadingConsultations.isVisible = false
                }

                when (state) {
                    is ConsultationsUiState.Loading -> {
                        binding.tvLoadingConsultations.apply {
                            isVisible = true
                            text = getString(R.string.loading)
                        }
                    }
                    is ConsultationsUiState.SuccessPhysio -> {
                        binding.rvAllConsultations.isVisible = true
                        physioAdapter.submitList(state.all)
                    }
                    is ConsultationsUiState.SuccessPatient -> {
                        binding.tvPendingTitle.isVisible = true
                        binding.rvPending.isVisible      = true
                        pendingAdapter.submitList(state.pending)

                        binding.tvHistoryTitle.isVisible = true
                        binding.rvHistory.isVisible      = true
                        historyAdapter.submitList(state.history)
                    }
                    is ConsultationsUiState.Error -> {
                        binding.tvLoadingConsultations.apply {
                            isVisible = true
                            text = state.message
                        }
                    }
                }
            }
        }

        viewModel.loadConsultations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Suppress("UNCHECKED_CAST")
class ConsultationsViewModelFactory(app: PhysioApp) : androidx.lifecycle.ViewModelProvider.Factory {
    private val repo    = PhysioRepository(RemoteDataSource())
    private val session = app.sessionManager

    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return ConsultationsViewModel(repo, session) as T
    }
}

package edu.matiasborra.physiocare.ui.features.consultations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.models.AppointmentFlat
import edu.matiasborra.physiocare.databinding.FragmentConsultationsBinding
import edu.matiasborra.physiocare.ui.features.consultations.adapter.ConsultationAdapter
import edu.matiasborra.physiocare.ui.features.patients.detail.AppointmentDetailFragment
import kotlinx.coroutines.launch

class ConsultationsFragment : Fragment() {

    private var _binding: FragmentConsultationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ConsultationsViewModel> {
        ConsultationsViewModelFactory(requireActivity().application as PhysioApp)
    }

    private lateinit var physioAdapter: ConsultationAdapter
    private lateinit var pendingAdapter: ConsultationAdapter
    private lateinit var historyAdapter: ConsultationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultationsBinding.inflate(inflater, container, false)

        physioAdapter = ConsultationAdapter { appointment -> openAppointmentDetail(appointment) }
        pendingAdapter = ConsultationAdapter { appointment -> openAppointmentDetail(appointment) }
        historyAdapter = ConsultationAdapter { appointment -> openAppointmentDetail(appointment) }

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
                binding.rvAllConsultations.isVisible = false
                binding.tvPendingTitle.isVisible = false
                binding.rvPending.isVisible = false
                binding.tvHistoryTitle.isVisible = false
                binding.rvHistory.isVisible = false
                binding.tvLoadingConsultations.isVisible = false
                binding.fabAddAppointment.isVisible = false

                when (state) {
                    is ConsultationsUiState.Loading -> {
                        binding.tvLoadingConsultations.isVisible = true
                        binding.tvLoadingConsultations.text = getString(R.string.loading)
                    }
                    is ConsultationsUiState.SuccessPhysio -> {
                        binding.rvAllConsultations.isVisible = true
                        binding.fabAddAppointment.isVisible = true
                        physioAdapter.submitList(state.all)
                    }
                    is ConsultationsUiState.SuccessPatient -> {
                        binding.tvPendingTitle.isVisible = true
                        binding.rvPending.isVisible = true
                        pendingAdapter.submitList(state.pending)

                        binding.tvHistoryTitle.isVisible = true
                        binding.rvHistory.isVisible = true
                        historyAdapter.submitList(state.history)
                    }
                    is ConsultationsUiState.Error -> {
                        binding.tvLoadingConsultations.isVisible = true
                        binding.tvLoadingConsultations.text = state.message
                    }
                }
            }
        }

        binding.fabAddAppointment.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.nav_host_container, CreateAppointmentFragment())
                addToBackStack(null)
            }
        }

        viewModel.loadConsultations()
    }

    private fun openAppointmentDetail(appointment: AppointmentFlat) {
        val bundle = Bundle().apply {
            putSerializable("appointment", appointment)
        }
        val detailFragment = AppointmentDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable("appointment", appointment)
            }
        }
        parentFragmentManager.commit {
            replace(R.id.nav_host_container, detailFragment)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ConsultationsViewModelFactory(
    private val app: PhysioApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = app.physioRepo
        val session = app.sessionManager
        return ConsultationsViewModel(repo, session) as T
    }
}

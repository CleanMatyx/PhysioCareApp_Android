// File: app/src/main/java/edu/matiasborra/physiocare/ui/main/patients/detail/PatientDetailFragment.kt
package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentPatientDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PatientDetailFragment : Fragment(R.layout.fragment_patient_detail) {

    companion object {
        private const val ARG_PATIENT_ID = "patient_id"
        fun newInstance(id: String) = PatientDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_PATIENT_ID, id) }
        }
    }

    private var _binding: FragmentPatientDetailBinding? = null
    private val binding get() = _binding!!
    private val app by lazy { requireActivity().application as PhysioApp }

    private val viewModel by viewModels<PatientDetailViewModel> {
        PatientDetailViewModel.Factory(
            repo    = app.physioRepo,
            session = app.sessionManager,
            patientId = arguments?.getString(ARG_PATIENT_ID).orEmpty()
        )
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientDetailBinding.bind(view)

        // 1) RecyclerView de citas
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appointmentAdapter
        }

        // 2) Ajusta título y visibilidad según rol
        lifecycleScope.launch {
            val sd = app.sessionManager.sessionFlow.firstOrNull()
            val role = sd?.role.orEmpty()
            val username = sd?.username.orEmpty()
            if (role == "patient") {
                binding.tvTitle.text = getString(R.string.perfil_de, username)
            } else {
                binding.tvTitle.text = getString(
                    R.string.patient_name_format,
                    arguments?.getString(ARG_PATIENT_ID).orEmpty(),
                    ""
                )
            }
            binding.containerAppointments.isVisible = (role != "patient")
        }

        // 3) Observa estado
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PatientDetailUiState.Loading -> {
                        // opcional: show loader
                    }
                    is PatientDetailUiState.Success -> {
                        // datos básicos
                        binding.tvName.text = getString(
                            R.string.patient_name_format,
                            state.patient.name,
                            state.patient.surname
                        )
                        binding.tvBirth.text = getString(
                            R.string.patient_birth_format,
                            state.patient.birthDate
                        )
                        binding.tvAddress.text   = state.patient.address
                        binding.tvInsurance.text = state.patient.insuranceNumber
                        binding.tvEmail.text     = state.patient.email

                        // expediente
                        binding.tvMedicalRecord.text = state.medicalRecord

                        // citas (solo si está visible el contenedor)
                        if (binding.containerAppointments.isVisible) {
                            appointmentAdapter.submitList(state.appointments)
                        }
                    }
                    is PatientDetailUiState.Error -> {
                        binding.tvMedicalRecord.text = state.message
                    }
                }
            }
        }

        // 4) Dispara carga
        viewModel.loadPatientAndRecords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

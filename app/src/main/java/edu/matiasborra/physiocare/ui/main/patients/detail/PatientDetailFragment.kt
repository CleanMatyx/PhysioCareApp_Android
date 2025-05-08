package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentPatientDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
            repo = app.physioRepo,
            session = app.sessionManager,
            patientId = requireArguments().getString(ARG_PATIENT_ID)!!
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientDetailBinding.bind(view)

//        viewLifecycleOwner.lifecycleScope.launch {
//            val sd = app.sessionManager.sessionFlow.firstOrNull()
//            val role = sd?.role.orEmpty()
//            val user = sd?.username.orEmpty()
//
//            binding.tvTitle.text = if (role == "patient") {
//                getString(R.string.perfil_de, user)
//            } else {
//                getString(
//                    R.string.patient_name_format,
//                    requireArguments().getString(ARG_PATIENT_ID).orEmpty(),
//                    ""
//                )
//            }
//            //binding.containerAppointments.isVisible = role != "patient"
//        }

        viewModel.loadPatientAndRecords()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.tvError.isVisible = false

                when (state) {
                    is PatientDetailUiState.Loading -> {
                        // TODO: agregar loader si querés
                    }
                    is PatientDetailUiState.Success -> {
                        val p = state.patient
                        val r = state.records

                        // Datos del paciente
                        binding.tvFullName.text = getString(R.string.patient_name_format, p.name, p.surname)
                        binding.tvBirthDate.text = getString(R.string.patient_birth_format, p.birthDate)
                        binding.tvAddress.text = getString(R.string.patient_address_format, p.address)
                        binding.tvNuss.text = getString(R.string.patient_insurance_format, p.insuranceNumber)
                        binding.tvEmail.text = getString(R.string.patient_email_format, p.email)
                        binding.tvMedicalHistory.text = r.firstOrNull()?.medicalRecord

                        Log.d("PatientDetailFragment", "Records: $r")
                        // Tratamientos pasados
                        if (binding.containerAppointments.isVisible) {
                            val pastAppointments = r
                                .flatMap { it.appointments }
                                .filter { app ->
                                    try {
                                        val date = SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                            Locale.getDefault()
                                        ).parse(app.date)
                                        date != null && date.before(Date())
                                    } catch (e: Exception) {
                                        false
                                    }
                                }

                            binding.treatmentContainer.removeAllViews()

                            pastAppointments.forEach { appointment ->
                                val treatmentView = TextView(requireContext()).apply {
                                    text = "• ${appointment.treatment}"
                                    textSize = 16f
                                    setPadding(0, 4, 0, 4)
                                }
                                binding.treatmentContainer.addView(treatmentView)
                            }

                            if (pastAppointments.isEmpty()) {
                                val emptyView = TextView(requireContext()).apply {
                                    text = "Sin tratamientos previos registrados"
                                    textSize = 15f
                                    setPadding(0, 8, 0, 8)
                                }
                                binding.treatmentContainer.addView(emptyView)
                            }
                        }
                    }
                    is PatientDetailUiState.Error -> {
                        binding.tvError.isVisible = true
                        binding.tvError.text = state.message
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

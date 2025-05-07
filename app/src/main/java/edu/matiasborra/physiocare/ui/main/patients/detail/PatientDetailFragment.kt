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
            app,
            arguments?.getString(ARG_PATIENT_ID).orEmpty()
        )
    }

    private lateinit var appointmentAdapter: AppointmentAdapter
    private var userRole: String = ""
    private var username: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientDetailBinding.bind(view)

        // 1) Preparamos RecyclerView de citas
        appointmentAdapter = AppointmentAdapter()
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appointmentAdapter
        }

        // 2) Leemos sesión para saber rol y nombre de usuario
        viewLifecycleOwner.lifecycleScope.launch {
            val sd = app.sessionManager.sessionFlow.firstOrNull()
            sd?.let {
                username = it.username.orEmpty()
                userRole = it.role.orEmpty()
            }

            // 3) Ponemos el título según rol:
            if (userRole == "patient") {
                binding.tvTitle.text = getString(R.string.perfil_de, username)
            } else {
                binding.tvTitle.text = getString(
                    R.string.patient_name_format,
                    arguments?.getString(ARG_PATIENT_ID).orEmpty(), ""
                )
            }

            // 4) Ocultamos TODO el bloque de citas si es patient
            binding.containerAppointments.isVisible = (userRole != "patient")

            // 5) Cargamos datos
            viewModel.loadPatientAndRecords()
        }

        // 6) Observamos estado
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PatientDetailUiState.Loading -> { /* mostrar loader si quieres */ }
                    is PatientDetailUiState.Success -> {
                        val p = state.patient
                        // rellenamos datos básicos
                        binding.tvName.text = getString(
                            R.string.patient_name_format, p.name, p.surname
                        )
                        binding.tvBirth.text = getString(
                            R.string.patient_birth_format, p.birthDate
                        )
                        binding.tvAddress.text   = p.address
                        binding.tvInsurance.text = p.insuranceNumber
                        binding.tvEmail.text     = p.email

                        // rellenamos citas solo si está visible el contenedor
                        if (binding.containerAppointments.isVisible) {
                            appointmentAdapter.submitList(state.appointments)
                        }
                    }
                    is PatientDetailUiState.Error -> {
                        /* mostrar mensaje de error */
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

package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentPatientDetailBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientDetailBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PatientDetailUiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.tvError   .isVisible = false
                    }
                    is PatientDetailUiState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.tvError   .isVisible = false

                        binding.tvName .text = getString(
                            R.string.patient_name_format,
                            state.patient.name,
                            state.patient.surname
                        )
                        binding.tvBirth.text = getString(
                            R.string.patient_birth_format,
                            state.patient.birthDate
                        )
                        // hueco para poblar más datos
                    }
                    is PatientDetailUiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.tvError   .isVisible = true
                        binding.tvError   .text      = state.message
                    }
                }
            }
        }

        viewModel.loadPatientAndRecords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
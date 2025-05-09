package edu.matiasborra.physiocare.ui.main.patients

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import edu.matiasborra.physiocare.databinding.FragmentPatientsBinding
import edu.matiasborra.physiocare.ui.main.patients.detail.PatientDetailFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PatientsFragment : Fragment(R.layout.fragment_patients) {

    private val app by lazy { requireActivity().application as PhysioApp }
    private val viewModel by viewModels<PatientsViewModel> {
        PatientsViewModelFactory(app)
    }

    private var _binding: FragmentPatientsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PatientAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientsBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            val sd = app.sessionManager.sessionFlow.firstOrNull()
            val role = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

            Log.d("PatientsFragment", "onViewCreated: role=$role, userId=$userId")

            if (role == "patient") {
                parentFragmentManager.commit {
                    replace(
                        R.id.nav_host_container,
                        PatientDetailFragment.newInstance(userId)
                    )
                }
                return@launch
            }

            adapter = PatientAdapter { patient ->
                parentFragmentManager.commit {
                    replace(
                        R.id.nav_host_container,
                        PatientDetailFragment.newInstance(patient._id)
                    )
                    addToBackStack(null)
                }
            }

            binding.rvPatients.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@PatientsFragment.adapter
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is PatientsUiState.Loading -> {
                                binding.tvLoadingPatients.isVisible = true
                                binding.rvPatients.isVisible = false
                            }
                            is PatientsUiState.Success -> {
                                binding.tvLoadingPatients.isVisible = false
                                binding.rvPatients.isVisible = true
                                adapter.submitList(state.patients)
                            }
                            is PatientsUiState.Error -> {
                                binding.tvLoadingPatients.text = state.message
                                binding.tvLoadingPatients.isVisible = true
                                binding.rvPatients.isVisible = false
                            }
                        }
                    }
                }
            }

            viewModel.loadPatients()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Factory para crear PatientsViewModel, inyectando PhysioRepository y SessionManager.
 */
@Suppress("UNCHECKED_CAST")
class PatientsViewModelFactory(
    app: PhysioApp
) : ViewModelProvider.Factory {

    private val session: SessionManager = app.sessionManager
    private val repo: PhysioRepository = PhysioRepository(RemoteDataSource(), session)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientsViewModel::class.java)) {
            return PatientsViewModel(repo, session) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
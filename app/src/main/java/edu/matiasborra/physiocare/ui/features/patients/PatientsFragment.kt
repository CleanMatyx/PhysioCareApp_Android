package edu.matiasborra.physiocare.ui.features.patients

import android.os.Bundle
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
import edu.matiasborra.physiocare.data.api.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import edu.matiasborra.physiocare.databinding.FragmentPatientsBinding
import edu.matiasborra.physiocare.ui.features.patients.adapter.PatientAdapter
import edu.matiasborra.physiocare.ui.features.patients.detail.PatientDetailFragment
import edu.matiasborra.physiocare.utils.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Fragmento para mostrar la lista de pacientes.
 * Permite navegar a los detalles de un paciente o redirigir al detalle del usuario si es un paciente.
 *
 * @author Matias Borra
 */
class PatientsFragment : Fragment(R.layout.fragment_patients) {

    /**
     * Referencia a la aplicación para acceder a repositorios y sesión.
     */
    private val app by lazy { requireActivity().application as PhysioApp }

    /**
     * ViewModel para manejar la lógica de negocio del fragmento.
     */
    private val viewModel by viewModels<PatientsViewModel> {
        PatientsViewModelFactory(app)
    }

    /**
     * Enlace al layout del fragmento.
     */
    private var _binding: FragmentPatientsBinding? = null
    private val binding get() = _binding!!

    /**
     * Adaptador para mostrar la lista de pacientes.
     */
    private lateinit var adapter: PatientAdapter

    /**
     * Configura la vista del fragmento después de que se haya creado.
     *
     * @param view Vista creada.
     * @param savedInstanceState Estado guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPatientsBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            val sd = app.sessionManager.sessionFlow.firstOrNull()
            val role = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

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

    /**
     * Libera los recursos del binding al destruir la vista.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Fábrica para crear instancias de `PatientsViewModel`.
 * Inyecta el repositorio y el administrador de sesión necesarios.
 *
 * @param app Aplicación para obtener dependencias.
 */
@Suppress("UNCHECKED_CAST")
class PatientsViewModelFactory(
    app: PhysioApp
) : ViewModelProvider.Factory {

    /**
     * Administrador de sesión para manejar la autenticación.
     */
    private val session: SessionManager = app.sessionManager

    /**
     * Repositorio para acceder a los datos de los pacientes.
     */
    private val repo: PhysioRepository = PhysioRepository(RemoteDataSource(), session)

    /**
     * Crea una instancia de `PatientsViewModel`.
     *
     * @param modelClass Clase del ViewModel.
     * @return Instancia de `PatientsViewModel`.
     * @throws IllegalArgumentException Si la clase no es compatible.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientsViewModel::class.java)) {
            return PatientsViewModel(repo, session) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
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

/**
 * Fragmento para gestionar y mostrar las consultas de fisioterapia.
 *
 * Este fragmento utiliza un ViewModel para cargar y observar el estado de las consultas,
 * mostrando diferentes listas según el tipo de usuario (fisioterapeuta o paciente).
 *
 * @author Matias Borra
 */
class ConsultationsFragment : Fragment() {

    /**
     * Enlace al layout del fragmento.
     */
    private var _binding: FragmentConsultationsBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel asociado al fragmento para manejar la lógica de negocio.
     */
    private val viewModel by viewModels<ConsultationsViewModel> {
        ConsultationsViewModelFactory(requireActivity().application as PhysioApp)
    }

    private lateinit var physioAdapter: ConsultationAdapter
    private lateinit var pendingAdapter: ConsultationAdapter
    private lateinit var historyAdapter: ConsultationAdapter

    /**
     * Crea la vista del fragmento inflando el layout correspondiente.
     *
     * @param inflater Inflador de vistas.
     * @param container Contenedor padre.
     * @param savedInstanceState Estado guardado del fragmento.
     * @return Vista inflada del fragmento.
     */
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

    /**
     * Configura las vistas y observa el estado del ViewModel.
     *
     * @param view Vista creada.
     * @param savedInstanceState Estado guardado del fragmento.
     */
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

    /**
     * Abre el detalle de una cita seleccionada.
     *
     * @param appointment Objeto con los datos de la cita.
     */
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

    /**
     * Limpia el enlace al layout cuando la vista es destruida.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Fábrica para crear instancias de ConsultationsViewModel.
 *
 * @property app Aplicación principal que contiene los repositorios y gestores de sesión.
 * @author Matias Borra
 */
class ConsultationsViewModelFactory(
    private val app: PhysioApp
) : ViewModelProvider.Factory {
    /**
     * Crea una instancia de ConsultationsViewModel.
     *
     * @param modelClass Clase del ViewModel.
     * @return Instancia de ConsultationsViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = app.physioRepo
        val session = app.sessionManager
        return ConsultationsViewModel(repo, session) as T
    }
}
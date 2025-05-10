package edu.matiasborra.physiocare.ui.features.patients.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentAppointmentDetailBinding
import edu.matiasborra.physiocare.data.models.AppointmentFlat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragmento para mostrar los detalles de una cita.
 * Proporciona opciones para eliminar la cita o ver el perfil del fisioterapeuta.
 *
 * @author Matias Borra
 */
class AppointmentDetailFragment : Fragment() {

    /**
     * Enlace al layout del fragmento.
     */
    private var _binding: FragmentAppointmentDetailBinding? = null
    private val binding get() = _binding!!

    /**
     * Cita seleccionada cuyos detalles se mostrarán.
     */
    private var appointment: AppointmentFlat? = null

    /**
     * Referencia a la aplicación para acceder al repositorio.
     */
    private val app by lazy { requireActivity().application as PhysioApp }

    /**
     * Inicializa el fragmento y recupera los argumentos.
     *
     * @param savedInstanceState Estado guardado del fragmento.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointment = arguments?.getSerializable("appointment") as? AppointmentFlat
    }

    /**
     * Infla el layout del fragmento.
     *
     * @param inflater Inflador para crear la vista.
     * @param container Contenedor padre de la vista.
     * @param savedInstanceState Estado guardado del fragmento.
     * @return Vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Configura la vista después de que se haya creado.
     *
     * @param view Vista creada.
     * @param savedInstanceState Estado guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointment?.let {
            val formattedDate = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.format(parser.parse(it.date) ?: it.date)
            } catch (e: Exception) {
                it.date
            }

            binding.tvDetailDate.text = formattedDate
            binding.tvDetailPhysio.text = it.physioName
            binding.tvDetailPatient.text = it.patientName
            binding.tvDetailDiagnosis.text = it.diagnosis
            binding.tvDetailTreatment.text = it.treatment
            binding.tvDetailObservations.text = it.observations.ifBlank { getString(R.string.none) }

            binding.btnDeleteAppointment.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setMessage(getString(R.string.confirm_delete_message))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        lifecycleScope.launch {
                            try {
                                val deleteResult =
                                    app.physioRepo.deleteAppointment(appointment!!.id)
                                if (deleteResult.ok) {
                                    Toast.makeText(requireContext(),
                                        getString(R.string.appointment_deleted),
                                        Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.popBackStack()
                                } else {
                                    Toast.makeText(requireContext(),
                                        deleteResult.message ?: getString(R.string.delete_error),
                                        Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(),
                                    getString(R.string.delete_error_generic),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.no), null)
                    .show()
            }

            val physioId = it.physioId
            if (!physioId.isNullOrBlank()) {
                binding.btnViewPhysio.setOnClickListener { _ ->
                    parentFragmentManager.commit {
                        replace(R.id.nav_host_container,
                            PhysioDetailFragment.newInstance(physioId))
                        addToBackStack(null)
                    }
                }
            } else {
                binding.btnViewPhysio.isVisible = false
                binding.btnDeleteAppointment.isVisible = true
            }
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
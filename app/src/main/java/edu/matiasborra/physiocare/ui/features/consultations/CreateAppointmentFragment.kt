package edu.matiasborra.physiocare.ui.features.consultations

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.databinding.FragmentCreateAppointmentBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragmento para crear una nueva cita.
 * Proporciona una interfaz para seleccionar un paciente, fecha y detalles de la cita.
 * Maneja la lógica para guardar la cita en el repositorio.
 *
 * @author Matias Borra
 */
class CreateAppointmentFragment : Fragment() {

    /**
     * Enlace al layout del fragmento.
     */
    private var _binding: FragmentCreateAppointmentBinding? = null
    private val binding get() = _binding!!

    /**
     * Referencia a la aplicación para acceder al repositorio.
     */
    private val app by lazy { requireActivity().application as PhysioApp }

    /**
     * ID del paciente seleccionado.
     */
    private var selectedPatientId: String? = null

    /**
     * Infla el layout del fragmento.
     *
     * @param inflater Inflador para crear la vista.
     * @param container Contenedor padre de la vista.
     * @param savedInstanceState Estado guardado del fragmento.
     * @return Vista inflada del fragmento.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Configura la vista después de que se haya creado.
     *
     * @param view Vista creada.
     * @param savedInstanceState Estado guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadPatients()
        setupDatePicker()

        binding.btnSaveAppointment.setOnClickListener {
            createAppointment()
        }
    }

    /**
     * Carga la lista de pacientes desde el repositorio y la muestra en un spinner.
     */
    private fun loadPatients() {
        lifecycleScope.launch {
            try {
                val patients = app.physioRepo.getAllPatients()
                val names = patients.map { "${it.name} ${it.surname}" }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerPatients.adapter = adapter

                binding.spinnerPatients.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedPatientId = patients[position]._id
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando pacientes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Configura un selector de fecha para el campo de entrada de fecha.
     */
    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        binding.etDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                calendar.set(y, m, d)
                binding.etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    /**
     * Crea una nueva cita con los datos ingresados por el usuario.
     */
    private fun createAppointment() {
        val dateInput = binding.etDate.text.toString()
        val diagnosis = binding.etDiagnosis.text.toString()
        val treatment = binding.etTreatment.text.toString()
        val observations = binding.etObservations.text.toString()
        val patientId = selectedPatientId ?: return

        if (dateInput.isBlank() || diagnosis.isBlank() || treatment.isBlank()) {
            Toast.makeText(requireContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val dateIso = "${dateInput}T09:00:00Z"

        lifecycleScope.launch {
            try {
                val result = app.physioRepo.createAppointmentForPatient(
                    patientId, dateIso, diagnosis, treatment, observations
                )
                if (result.ok) {
                    Toast.makeText(requireContext(), "Cita creada correctamente", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), result.message ?: "Error inesperado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al crear cita", Toast.LENGTH_SHORT).show()
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
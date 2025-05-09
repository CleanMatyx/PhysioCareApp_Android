package edu.matiasborra.physiocare.ui.features.consultations

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.databinding.FragmentCreateAppointmentBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast

class CreateAppointmentFragment : Fragment() {

    private var _binding: FragmentCreateAppointmentBinding? = null
    private val binding get() = _binding!!

    private val app by lazy { requireActivity().application as PhysioApp }

    private var selectedPatientId: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadPatients()
        setupDatePicker()

        binding.btnSaveAppointment.setOnClickListener {
            createAppointment()
        }
    }

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
                        Log.d("CreateAppointmentFragment", "Selected patient ID: ${selectedPatientId}")
                        Log.d("CreateAppointmentFragment", "Selected patient name: ${names[position]}")
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando pacientes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        binding.etDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                calendar.set(y, m, d)
                binding.etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

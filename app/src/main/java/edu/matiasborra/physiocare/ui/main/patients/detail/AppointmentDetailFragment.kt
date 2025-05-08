package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentAppointmentDetailBinding
import edu.matiasborra.physiocare.data.remote.models.AppointmentFlat
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailFragment : Fragment() {

    private var _binding: FragmentAppointmentDetailBinding? = null
    private val binding get() = _binding!!
    private var appointment: AppointmentFlat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointment = arguments?.getSerializable("appointment") as? AppointmentFlat
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            binding.tvDetailObservations.text = it.observations.ifBlank { "Ninguna" }

            // Ver perfil fisio
            val physioId = it.physioId
            if (!physioId.isNullOrBlank()) {
                binding.btnViewPhysio.setOnClickListener { _ ->
                    parentFragmentManager.commit {
                        replace(R.id.nav_host_container, PhysioDetailFragment.newInstance(physioId))
                        addToBackStack(null)
                    }
                }
            } else {
                binding.btnViewPhysio.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

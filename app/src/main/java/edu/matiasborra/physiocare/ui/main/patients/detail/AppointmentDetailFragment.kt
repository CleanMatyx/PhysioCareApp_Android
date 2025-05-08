// File: AppointmentDetailFragment.kt
package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentAppointmentDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppointmentDetailFragment
    : Fragment(R.layout.fragment_appointment_detail) {

    companion object {
        private const val ARG_APPT_ID = "appt_id"
        fun newInstance(id: String) = AppointmentDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_APPT_ID, id) }
        }
    }

    private var _binding: FragmentAppointmentDetailBinding? = null
    private val b get() = _binding!!
    private val app by lazy { requireActivity().application as PhysioApp }
    private val repo by lazy { app.physioRepo }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAppointmentDetailBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            val token  = app.sessionManager.getToken.firstOrNull().orEmpty()
            val apptId = arguments?.getString(ARG_APPT_ID).orEmpty()

            // ==> ahora sí existe este método
            val resp = repo.getAppointmentDetail(token, apptId)

            if (resp.ok && resp.result != null) {
                val ap = resp.result

                b.tvDate.text         = ap.date
                b.tvDiagnosis.text    = ap.diagnosis
                b.tvTreatment.text    = ap.treatment
                b.tvObservations.text = ap.observations.orEmpty()

                // physio ya es un objeto AppointmentPhysio
                val physio = ap.physio
                val physioName = if (physio != null) {
                    "${physio.name} ${physio.surname}"
                } else getString(R.string.none)

                b.tvPhysioName.text = getString(
                    R.string.appointment_physio, physioName
                )

                b.btnViewPhysio.setOnClickListener {
                    parentFragmentManager.commit {
                        // usa el id real
                        replace(
                            R.id.nav_host_container,
                            PhysioDetailFragment.newInstance(physio?._id.orEmpty())
                        )
                        addToBackStack(null)
                    }
                }
            } else {
                // TODO: mostrar error en UI
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

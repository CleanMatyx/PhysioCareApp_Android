package edu.matiasborra.physiocare.ui.main.consultations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.remote.models.AppointmentFlat
import edu.matiasborra.physiocare.databinding.ItemConsultationBinding

class ConsultationAdapter
    : ListAdapter<AppointmentFlat, ConsultationAdapter.VH>(DiffCallback) {

    inner class VH(private val b: ItemConsultationBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AppointmentFlat) = with(b) {
            val ctx = root.context
            tvPatientName.text = ctx.getString(
                R.string.consultation_patient,
                item.patientName
            )
            tvPhysioName.text = ctx.getString(
                R.string.consultation_physio,
                item.physioName
            )
            tvDate.text = ctx.getString(
                R.string.consultation_date,
                item.date
            )
            tvDiagnosis.text = ctx.getString(
                R.string.consultation_diagnosis,
                item.diagnosis
            )
            tvTreatment.text = ctx.getString(
                R.string.consultation_treatment,
                item.treatment
            )
            tvObservations.text = ctx.getString(
                R.string.consultation_observations,
                item.observations ?: ""
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemConsultationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentFlat>() {
            override fun areItemsTheSame(a: AppointmentFlat, b: AppointmentFlat) =
                // comparamos por fecha+paciente (o el campo que sea único)
                a.patientName == b.patientName && a.date == b.date
            override fun areContentsTheSame(a: AppointmentFlat, b: AppointmentFlat) =
                a == b
        }
    }
}

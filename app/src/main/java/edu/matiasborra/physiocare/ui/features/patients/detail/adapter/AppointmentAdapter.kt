package edu.matiasborra.physiocare.ui.features.patients.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.models.AppointmentItem
import edu.matiasborra.physiocare.databinding.ItemAppointmentBinding

class AppointmentAdapter
    : ListAdapter<AppointmentItem, AppointmentAdapter.VH>(DiffCallback) {

    inner class VH(private val binding: ItemAppointmentBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppointmentItem) {
            with(binding) {
                val ctx = root.context

                // Si physio es un objeto, extraemos nombre y apellido
                val physioName = item.physio
                    ?.let { "${it.name} ${it.surname}" }
                    ?: ctx.getString(R.string.none)

                tvPhysio.text = ctx.getString(
                    R.string.appointment_physio,
                    physioName
                )
                tvDate.text = ctx.getString(R.string.appointment_date) + ": " + item.date
                tvDiagnosis.text = ctx.getString(
                    R.string.appointment_diagnosis,
                    item.diagnosis
                )
                tvTreatment.text = ctx.getString(
                    R.string.appointment_treatment,
                    item.treatment
                )
                tvObservations.text = ctx.getString(
                    R.string.appointment_observations,
                    item.observations ?: ctx.getString(R.string.none)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentItem>() {
            override fun areItemsTheSame(old: AppointmentItem, new: AppointmentItem) =
                old._id == new._id    // comparamos por identificador único

            override fun areContentsTheSame(old: AppointmentItem, new: AppointmentItem) =
                old == new
        }
    }
}

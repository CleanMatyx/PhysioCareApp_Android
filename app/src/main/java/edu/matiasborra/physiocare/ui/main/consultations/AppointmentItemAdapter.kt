package edu.matiasborra.physiocare.ui.main.consultations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.data.remote.models.AppointmentItem
import edu.matiasborra.physiocare.databinding.ItemAppointmentSimpleBinding

class AppointmentItemAdapter(
    private val onClick: (AppointmentItem) -> Unit = {}
) : ListAdapter<AppointmentItem, AppointmentItemAdapter.VH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAppointmentSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemAppointmentSimpleBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppointmentItem) {
            binding.tvDate.text      = item.date
            binding.tvDiagnosis.text = item.diagnosis
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentItem>() {
            override fun areItemsTheSame(a: AppointmentItem, b: AppointmentItem) =
                a.date == b.date && a.diagnosis == b.diagnosis
            override fun areContentsTheSame(a: AppointmentItem, b: AppointmentItem) = a == b
        }
    }
}

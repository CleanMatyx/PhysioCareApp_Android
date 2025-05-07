package edu.matiasborra.physiocare.ui.main.patients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.remote.models.PatientItem
import edu.matiasborra.physiocare.databinding.ItemPatientBinding

class PatientAdapter(
    private val onClick: (PatientItem) -> Unit
) : ListAdapter<PatientItem, PatientAdapter.VH>(DiffCallback) {

    inner class VH(private val binding: ItemPatientBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PatientItem) {
            val ctx = binding.root.context
            binding.tvPatientName.text = ctx.getString(
                R.string.patient_name_format,
                item.name,
                item.surname
            )
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPatientBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PatientItem>() {
            override fun areItemsTheSame(a: PatientItem, b: PatientItem) = a._id == b._id
            override fun areContentsTheSame(a: PatientItem, b: PatientItem) = a == b
        }
    }
}
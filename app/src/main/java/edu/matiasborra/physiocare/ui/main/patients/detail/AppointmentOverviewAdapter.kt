package edu.matiasborra.physiocare.ui.main.patients.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.data.remote.models.AppointmentItem
import edu.matiasborra.physiocare.databinding.ItemAppointmentOverviewBinding

class AppointmentOverviewAdapter(
    private val onClick: (AppointmentItem) -> Unit
) : ListAdapter<AppointmentItem, AppointmentOverviewAdapter.VH>(DiffCallback) {

    inner class VH(private val b: ItemAppointmentOverviewBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AppointmentItem) = with(b) {
            tvOverviewDate.text      = item.date
            tvOverviewDiagnosis.text = item.diagnosis
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemAppointmentOverviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentItem>() {
            override fun areItemsTheSame(o: AppointmentItem, n: AppointmentItem) =
                o._id == n._id
            override fun areContentsTheSame(o: AppointmentItem, n: AppointmentItem) =
                o == n
        }
    }
}

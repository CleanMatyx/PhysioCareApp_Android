package edu.matiasborra.physiocare.ui.features.consultations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.data.models.AppointmentFlat
import edu.matiasborra.physiocare.databinding.ItemConsultationCardBinding
import java.text.SimpleDateFormat
import java.util.*

class ConsultationAdapter(
    private val onItemClick: (AppointmentFlat) -> Unit
) : ListAdapter<AppointmentFlat, ConsultationAdapter.VH>(DiffCallback) {

    inner class VH(private val b: ItemConsultationCardBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AppointmentFlat) = with(b) {
            val dateFormatted = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.format(parser.parse(item.date)!!)
            } catch (e: Exception) {
                item.date
            }

            tvAppointmentTitle.text = "Cita el día $dateFormatted"
            tvAppointmentPhysio.text = "Fisioterapeuta: ${item.physioName}"

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemConsultationCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentFlat>() {
            override fun areItemsTheSame(oldItem: AppointmentFlat, newItem: AppointmentFlat): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AppointmentFlat, newItem: AppointmentFlat): Boolean {
                return oldItem == newItem
            }
        }
    }
}

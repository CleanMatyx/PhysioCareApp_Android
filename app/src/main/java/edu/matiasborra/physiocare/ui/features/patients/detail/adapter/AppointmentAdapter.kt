package edu.matiasborra.physiocare.ui.features.patients.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.models.AppointmentItem
import edu.matiasborra.physiocare.databinding.ItemAppointmentBinding

/**
 * Adaptador para mostrar una lista de citas en un RecyclerView.
 * Permite manejar y mostrar los detalles de cada cita.
 *
 * @author Matias Borra
 */
class AppointmentAdapter
    : ListAdapter<AppointmentItem, AppointmentAdapter.ViewHolder>(DiffCallback) {

    /**
     * ViewHolder que representa un elemento de la lista de citas.
     *
     * @param binding Enlace al layout del elemento.
     */
    inner class ViewHolder(private val binding: ItemAppointmentBinding)
        : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de una cita al elemento de la lista.
         *
         * @param item Datos de la cita a mostrar.
         */
        fun bind(item: AppointmentItem) {
            with(binding) {
                val ctx = root.context

                // Si physio es un objeto, extraigo nombre y apellido
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

    /**
     * Crea un nuevo ViewHolder para un elemento de la lista.
     *
     * @param parent Contenedor padre donde se añadirá el ViewHolder.
     * @param viewType Tipo de vista del elemento.
     * @return Nuevo ViewHolder creado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Vincula un ViewHolder con los datos de una cita.
     *
     * @param holder ViewHolder a vincular.
     * @param position Posición del elemento en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * Callback para calcular las diferencias entre dos listas de citas.
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentItem>() {
            /**
             * Verifica si dos citas tienen el mismo ID.
             *
             * @param old Primer elemento.
             * @param new Segundo elemento.
             * @return `true` si los IDs son iguales, de lo contrario `false`.
             */
            override fun areItemsTheSame(old: AppointmentItem, new: AppointmentItem) =
                old._id == new._id

            /**
             * Verifica si dos citas tienen el mismo contenido.
             *
             * @param old Primer elemento.
             * @param new Segundo elemento.
             * @return `true` si los contenidos son iguales, de lo contrario `false`.
             */
            override fun areContentsTheSame(old: AppointmentItem, new: AppointmentItem) =
                old == new
        }
    }
}
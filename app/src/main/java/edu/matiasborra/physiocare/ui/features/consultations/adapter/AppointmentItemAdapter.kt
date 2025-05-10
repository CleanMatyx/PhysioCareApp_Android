package edu.matiasborra.physiocare.ui.features.consultations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.data.models.AppointmentItem
import edu.matiasborra.physiocare.databinding.ItemAppointmentSimpleBinding

/**
 * Adaptador para mostrar una lista de citas en un RecyclerView.
 *
 * @property onClick Acción a ejecutar cuando se selecciona un elemento de la lista.
 * @constructor Crea una instancia del adaptador con un callback opcional.
 * @param onClick Callback que recibe un objeto AppointmentItem al hacer clic en un elemento.
 * @see ListAdapter
 * @see RecyclerView
 * @see AppointmentItem
 * @see ItemAppointmentSimpleBinding
 * @see DiffUtil.ItemCallback
 *
 * @author Matias Borra
 */
class AppointmentItemAdapter(
    private val onClick: (AppointmentItem) -> Unit = {}
) : ListAdapter<AppointmentItem, AppointmentItemAdapter.ViewHolder>(DiffCallback) {

    /**
     * Crea una nueva instancia del ViewHolder inflando el layout correspondiente.
     *
     * @param parent Vista padre donde se añadirá el ViewHolder.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return Instancia de VH.
     * @author Matias Borra
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Vincula un elemento de la lista al ViewHolder.
     *
     * @param holder ViewHolder que se actualizará.
     * @param position Posición del elemento en la lista.
     * @author Matias Borra
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder para manejar la vista de cada elemento de la lista.
     *
     * @property binding Enlace al layout del elemento.
     * @constructor Crea una instancia del ViewHolder con el binding proporcionado.
     * @param binding Objeto de enlace al layout del elemento.
     * @author Matias Borra
     */
    inner class ViewHolder(private val binding: ItemAppointmentSimpleBinding)
        : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de un AppointmentItem a los elementos de la vista.
         *
         * @param item Objeto AppointmentItem con los datos a mostrar.
         * @author Matias Borra
         */
        fun bind(item: AppointmentItem) {
            binding.tvDate.text = item.date
            binding.tvDiagnosis.text = item.diagnosis
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        /**
         * Callback para calcular las diferencias entre elementos de la lista.
         *
         * @see DiffUtil.ItemCallback
         * @author Matias Borra
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentItem>() {
            /**
             * Verifica si dos elementos representan el mismo objeto.
             *
             * @param a Primer elemento.
             * @param b Segundo elemento.
             * @return `true` si los elementos son el mismo objeto, `false` en caso contrario.
             * @author Matias Borra
             */
            override fun areItemsTheSame(a: AppointmentItem, b: AppointmentItem) =
                a.date == b.date && a.diagnosis == b.diagnosis

            /**
             * Verifica si el contenido de dos elementos es el mismo.
             *
             * @param a Primer elemento.
             * @param b Segundo elemento.
             * @return `true` si el contenido es igual, `false` en caso contrario.
             * @author Matias Borra
             */
            override fun areContentsTheSame(a: AppointmentItem, b: AppointmentItem) = a == b
        }
    }
}
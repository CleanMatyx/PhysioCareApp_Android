package edu.matiasborra.physiocare.ui.features.patients.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.data.models.AppointmentItem
import edu.matiasborra.physiocare.databinding.ItemAppointmentOverviewBinding

/**
 * Adaptador para mostrar una lista de citas en un RecyclerView.
 * Permite manejar eventos de clic en los elementos de la lista.
 *
 * @author Matias Borra
 */
class AppointmentOverviewAdapter(
    private val onClick: (AppointmentItem) -> Unit
) : ListAdapter<AppointmentItem, AppointmentOverviewAdapter.ViewHolder>(DiffCallback) {

    /**
     * ViewHolder que representa un elemento de la lista de citas.
     *
     * @param b Enlace al layout del elemento.
     */
    inner class ViewHolder(private val b: ItemAppointmentOverviewBinding)
        : RecyclerView.ViewHolder(b.root) {

        /**
         * Vincula los datos de una cita al elemento de la lista.
         *
         * @param item Datos de la cita a mostrar.
         */
        fun bind(item: AppointmentItem) = with(b) {
            tvOverviewDate.text      = item.date
            tvOverviewDiagnosis.text = item.diagnosis
            root.setOnClickListener { onClick(item) }
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
        val b = ItemAppointmentOverviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(b)
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
             * @param o Primer elemento.
             * @param n Segundo elemento.
             * @return `true` si los IDs son iguales, de lo contrario `false`.
             */
            override fun areItemsTheSame(o: AppointmentItem, n: AppointmentItem) =
                o._id == n._id

            /**
             * Verifica si dos citas tienen el mismo contenido.
             *
             * @param o Primer elemento.
             * @param n Segundo elemento.
             * @return `true` si los contenidos son iguales, de lo contrario `false`.
             */
            override fun areContentsTheSame(o: AppointmentItem, n: AppointmentItem) =
                o == n
        }
    }
}
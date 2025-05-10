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

/**
 * Adaptador para mostrar una lista de consultas en un RecyclerView.
 *
 * @property onItemClick Acción a ejecutar cuando se selecciona un elemento de la lista.
 * @constructor Crea una instancia del adaptador con un callback opcional.
 * @param onItemClick Callback que recibe un objeto AppointmentFlat al hacer clic en un elemento.
 *
 * @author Matias Borra
 */
class ConsultationAdapter(
    private val onItemClick: (AppointmentFlat) -> Unit
) : ListAdapter<AppointmentFlat, ConsultationAdapter.ViewHolder>(DiffCallback) {

    /**
     * ViewHolder para manejar la vista de cada elemento de la lista.
     *
     * @property binding Enlace al layout del elemento.
     * @constructor Crea una instancia del ViewHolder con el binding proporcionado.
     * @param binding Objeto de enlace al layout del elemento.
     * @author Matias Borra
     */
    inner class ViewHolder(private val binding: ItemConsultationCardBinding)
        : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de un AppointmentFlat a los elementos de la vista.
         *
         * @param item Objeto AppointmentFlat con los datos a mostrar.
         * @author Matias Borra
         */
        fun bind(item: AppointmentFlat) = with(binding) {
            val dateFormatted = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.format(parser.parse(item.date)!!)
            } catch (e: Exception) {
                item.date
            }

            // Strings hardcodeados ya que no paso contexto a la clase
            tvAppointmentTitle.text = "Cita el día $dateFormatted"
            tvAppointmentPhysio.text = "Fisioterapeuta: ${item.physioName}"

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    /**
     * Crea una nueva instancia del ViewHolder inflando el layout correspondiente.
     *
     * @param parent Vista padre donde se añadirá el ViewHolder.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return Instancia de VH.
     * @author Matias Borra
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemConsultationCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(b)
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

    companion object {
        /**
         * Callback para calcular las diferencias entre elementos de la lista.
         *
         * @see DiffUtil.ItemCallback
         * @author Matias Borra
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<AppointmentFlat>() {
            /**
             * Verifica si dos elementos representan el mismo objeto.
             *
             * @param oldItem Primer elemento.
             * @param newItem Segundo elemento.
             * @return `true` si los elementos son el mismo objeto, `false` en caso contrario.
             * @author Matias Borra
             */
            override fun areItemsTheSame(oldItem: AppointmentFlat, newItem: AppointmentFlat): Boolean {
                return oldItem.id == newItem.id
            }

            /**
             * Verifica si el contenido de dos elementos es el mismo.
             *
             * @param oldItem Primer elemento.
             * @param newItem Segundo elemento.
             * @return `true` si el contenido es igual, `false` en caso contrario.
             * @author Matias Borra
             */
            override fun areContentsTheSame(oldItem: AppointmentFlat, newItem: AppointmentFlat): Boolean {
                return oldItem == newItem
            }
        }
    }
}
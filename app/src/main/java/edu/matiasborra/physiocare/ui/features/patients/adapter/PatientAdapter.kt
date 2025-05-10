package edu.matiasborra.physiocare.ui.features.patients.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.data.models.PatientItem
import edu.matiasborra.physiocare.databinding.ItemPatientCardBinding

/**
 * Adaptador para mostrar una lista de pacientes en un RecyclerView.
 * Permite manejar eventos de clic en los elementos de la lista.
 *
 * @param onClick Acción a ejecutar cuando se selecciona un paciente.
 * @author Matias Borra
 */
class PatientAdapter(
    private val onClick: (PatientItem) -> Unit
) : ListAdapter<PatientItem, PatientAdapter.ViewHolder>(DiffCallback) {

    /**
     * ViewHolder que representa un elemento de la lista de pacientes.
     *
     * @param binding Enlace al layout del elemento.
     */
    inner class ViewHolder(private val binding: ItemPatientCardBinding)
        : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos de un paciente al elemento de la lista.
         *
         * @param item Datos del paciente a mostrar.
         */
        fun bind(item: PatientItem) {
            val ctx = binding.root.context
            binding.tvPatientName.text = ctx.getString(
                R.string.patient_name_format,
                item.name,
                item.surname
            )
            binding.tvPatientInsurance.text = ctx.getString(
                R.string.patient_insurance_format,
                item.insuranceNumber
            )
            binding.root.setOnClickListener { onClick(item) }
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
        val binding = ItemPatientCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Vincula un ViewHolder con los datos de un paciente.
     *
     * @param holder ViewHolder a vincular.
     * @param position Posición del elemento en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * Callback para calcular las diferencias entre dos listas de pacientes.
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<PatientItem>() {
            /**
             * Verifica si dos pacientes tienen el mismo ID.
             *
             * @param a Primer paciente.
             * @param b Segundo paciente.
             * @return `true` si los IDs son iguales, de lo contrario `false`.
             */
            override fun areItemsTheSame(a: PatientItem, b: PatientItem) = a._id == b._id

            /**
             * Verifica si dos pacientes tienen el mismo contenido.
             *
             * @param a Primer paciente.
             * @param b Segundo paciente.
             * @return `true` si los contenidos son iguales, de lo contrario `false`.
             */
            override fun areContentsTheSame(a: PatientItem, b: PatientItem) = a == b
        }
    }
}
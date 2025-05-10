package edu.matiasborra.physiocare.ui.features.patients.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentPhysioDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Fragmento para mostrar los detalles de un fisioterapeuta.
 * Carga y muestra información del fisioterapeuta, incluyendo su nombre, especialidad y otros datos.
 *
 * @author Matias Borra
 */
class PhysioDetailFragment : Fragment(R.layout.fragment_physio_detail) {

    companion object {
        /**
         * Clave para pasar el ID del fisioterapeuta como argumento.
         */
        private const val ARG_PHYSIO_ID = "physio_id"

        /**
         * Crea una nueva instancia del fragmento con el ID del fisioterapeuta.
         *
         * @param id ID del fisioterapeuta.
         * @return Instancia del fragmento.
         */
        fun newInstance(id: String) = PhysioDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_PHYSIO_ID, id) }
        }
    }

    /**
     * Enlace al layout del fragmento.
     */
    private var _binding: FragmentPhysioDetailBinding? = null
    private val binding get() = _binding!!

    /**
     * Referencia a la aplicación para acceder al repositorio y la sesión.
     */
    private val app by lazy { requireActivity().application as PhysioApp }

    /**
     * Repositorio para obtener los datos del fisioterapeuta.
     */
    private val repo by lazy { app.physioRepo }

    /**
     * Configura la vista del fragmento después de que se haya creado.
     *
     * @param view Vista creada.
     * @param savedInstanceState Estado guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPhysioDetailBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            val token = app.sessionManager.getToken.firstOrNull().orEmpty()
            if (token.isEmpty()) {
                return@launch
            }
            val physioId = arguments?.getString(ARG_PHYSIO_ID).orEmpty()
            val resp = repo.getPhysio(token, physioId)
            if (resp.ok && resp.result != null) {
                val f = resp.result
                binding.tvPhysioName.text = "${f.name} ${f.surname}"
                binding.tvSpecialty.text = getString(R.string.physio_specialty, f.specialty)
                binding.tvLicense.text = getString(R.string.physio_license, f.licenseNumber)
                binding.tvEmail.text = f.email
            }
        }
    }

    /**
     * Libera los recursos del binding al destruir la vista.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
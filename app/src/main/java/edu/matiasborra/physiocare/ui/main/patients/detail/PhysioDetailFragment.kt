package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.FragmentPhysioDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PhysioDetailFragment : Fragment(R.layout.fragment_physio_detail) {

    companion object {
        private const val ARG_PHYSIO_ID = "physio_id"
        fun newInstance(id: String) = PhysioDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_PHYSIO_ID, id) }
        }
    }

    private var _binding: FragmentPhysioDetailBinding? = null
    private val binding get() = _binding!!
    private val app by lazy { requireActivity().application as PhysioApp }
    private val repo by lazy { app.physioRepo }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPhysioDetailBinding.bind(view)

        // Carga datos en un coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            // 1) Obtenemos token y physioId
            val token = app.sessionManager.getToken.firstOrNull().orEmpty()
            Log.d("PhysioDetailFragment", "PhysioId: ${arguments?.getString(ARG_PHYSIO_ID)}")
            if (token.isEmpty()) {
                Log.d("PhysioDetailFragment", "Token is empty, aborting...")
                return@launch
            }
            val physioId = arguments?.getString(ARG_PHYSIO_ID).orEmpty()

            // 2) Llamamos al repositorio
            val resp = repo.getPhysio(token, physioId)
            if (resp.ok && resp.result != null) {
                val f = resp.result

                // 3) Cargamos imagen con Glide (si existiera URL)
                f.image?.takeIf { it.isNotBlank() }?.let { url ->
                    Glide.with(this@PhysioDetailFragment)
                        .load(url)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.ivProfile)
                }

                // 4) Rellenamos campos
                binding.tvPhysioName.text = "${f.name} ${f.surname}"
                binding.tvSpecialty.text  = getString(R.string.physio_specialty, f.specialty)
                binding.tvLicense.text    = getString(R.string.physio_license, f.licenseNumber)
                binding.tvEmail.text      = f.email
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

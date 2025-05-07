package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.databinding.ActivityPatientDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PatientDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PATIENT_ID = "patient_id"
    }

    private lateinit var binding: ActivityPatientDetailBinding
    private val session: SessionManager by lazy { (application as PhysioApp).sessionManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Esperamos a tener la sesión para decidir qué ID usar
        lifecycleScope.launch {
            val sd = session.sessionFlow.firstOrNull()
            val role   = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

            // Si es physio tomamos el extra, si no usamos nuestro propio userId
            val idToLoad = if (role == "physio") {
                intent.getStringExtra(EXTRA_PATIENT_ID).orEmpty()
            } else {
                userId
            }

            // Sólo procedemos si tenemos un ID válido
            if (idToLoad.isBlank()) {
                finish() // nada que mostrar
                return@launch
            }

            // Cargamos el fragmento con el ID adecuado
            if (savedInstanceState == null) {
                supportFragmentManager.commit {
                    replace(
                        R.id.detail_fragment_container,
                        PatientDetailFragment.newInstance(idToLoad)
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
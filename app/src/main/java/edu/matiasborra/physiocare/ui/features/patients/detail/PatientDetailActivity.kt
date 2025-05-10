package edu.matiasborra.physiocare.ui.features.patients.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.databinding.ActivityPatientDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Actividad para mostrar los detalles de un paciente.
 * Gestiona la carga del fragmento de detalles según el rol del usuario.
 *
 * @author Matias Borra
 */
class PatientDetailActivity : AppCompatActivity() {

    companion object {
        /**
         * Clave para pasar el ID del paciente como extra.
         */
        const val EXTRA_PATIENT_ID = "patient_id"
    }

    /**
     * Enlace al layout de la actividad.
     */
    private lateinit var binding: ActivityPatientDetailBinding

    /**
     * Administrador de sesión para obtener información del usuario.
     */
    private val session: SessionManager by lazy { (application as PhysioApp).sessionManager }

    /**
     * Inicializa la actividad y configura la barra de herramientas.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            val sd = session.sessionFlow.firstOrNull()
            val role = sd?.role.orEmpty()
            val userId = sd?.userId.orEmpty()

            val idToLoad = if (role == "physio") {
                intent.getStringExtra(EXTRA_PATIENT_ID).orEmpty()
            } else {
                userId
            }

            if (idToLoad.isBlank()) {
                finish()
                return@launch
            }

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

    /**
     * Maneja la acción de navegación hacia atrás en la barra de herramientas.
     *
     * @return `true` si la acción fue manejada correctamente.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
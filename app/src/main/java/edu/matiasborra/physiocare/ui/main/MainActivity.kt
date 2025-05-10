package edu.matiasborra.physiocare.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.auth.ui.LoginActivity
import edu.matiasborra.physiocare.databinding.ActivityMainBinding
import edu.matiasborra.physiocare.ui.features.consultations.ConsultationsFragment
import edu.matiasborra.physiocare.ui.features.patients.PatientsFragment
import edu.matiasborra.physiocare.ui.features.patients.detail.PhysioDetailFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Actividad principal de la aplicación.
 * Gestiona la navegación entre fragmentos y la sesión del usuario.
 *
 * @author Matias Borra
 */
class MainActivity : AppCompatActivity() {

    /**
     * Enlace al layout de la actividad.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Referencia a la aplicación para acceder a dependencias.
     */
    private val app by lazy { application as PhysioApp }

    /**
     * Administrador de sesión para manejar la autenticación.
     */
    private val session by lazy { app.sessionManager }

    /**
     * Rol actual del usuario autenticado.
     */
    private var currentRole: String? = null

    /**
     * Configura la actividad al ser creada.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, 0)
            insets
        }

        setSupportActionBar(binding.toolbar)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        checkSessionAndInitUI()
        setupBottomNav()
    }

    /**
     * Verifica la sesión del usuario y configura la interfaz de usuario.
     */
    private fun checkSessionAndInitUI() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                session.sessionFlow.collect { data ->
                    val token = data.token
                    val role = data.role.orEmpty()
                    val username = data.username.orEmpty()

                    if (token.isNullOrEmpty()) {
                        startLogin()
                    } else {
                        currentRole = role
                        updateBottomNavLabels(role)
                        showConsultations()
                        if (role == "physio") {
                            binding.toolbar.title = "Bienvenido, $username"
                        } else {
                            binding.toolbar.title = getString(R.string.consultations_title)
                        }
                    }
                }
            }
        }
    }

    /**
     * Actualiza las etiquetas del menú de navegación inferior según el rol del usuario.
     *
     * @param role Rol del usuario.
     */
    private fun updateBottomNavLabels(role: String) {
        val menu = binding.bottomNav.menu
        menu.clear()
        if (role == "physio") {
            menu.add(0, R.id.nav_consultations, 0, getString(R.string.menu_manage_appointments))
                .setIcon(R.drawable.ic_appointments_icon)
            menu.add(0, R.id.nav_patients, 1, getString(R.string.menu_my_profile))
                .setIcon(R.drawable.ic_profile_physio_icon)
            menu.add(0, R.id.nav_physio_patients, 2, getString(R.string.menu_patients))
                .setIcon(R.drawable.ic_person)
        } else {
            menu.add(0, R.id.nav_consultations, 0, getString(R.string.menu_my_consultations))
                .setIcon(R.drawable.ic_record_icon)
            menu.add(0, R.id.nav_patients, 1, getString(R.string.menu_my_profile))
                .setIcon(R.drawable.ic_user_icon)
        }
    }

    /**
     * Configura el menú de navegación inferior.
     */
    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_consultations -> {
                    showConsultations()
                    true
                }
                R.id.nav_patients -> {
                    showPatients()
                    true
                }
                R.id.nav_physio_patients -> {
                    showPatientList()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Muestra el fragmento de consultas.
     */
    private fun showConsultations() {
        supportFragmentManager.commit {
            replace(binding.navHostContainer.id, ConsultationsFragment())
        }
    }

    /**
     * Muestra el fragmento de pacientes o el perfil del fisioterapeuta según el rol.
     */
    private fun showPatients() {
        supportFragmentManager.commit {
            if (currentRole == "physio") {
                val physioId = runBlocking { session.getUserId.first() }

                val fragment = PhysioDetailFragment()
                val bundle = Bundle()
                bundle.putString("physio_id", physioId)
                fragment.arguments = bundle
                Log.d("MainActivity", "Physio ID: $physioId")
                replace(binding.navHostContainer.id, fragment)
            } else {
                replace(binding.navHostContainer.id, PatientsFragment())
            }
        }
    }

    /**
     * Muestra la lista de pacientes.
     */
    private fun showPatientList() {
        supportFragmentManager.commit {
            replace(binding.navHostContainer.id, PatientsFragment())
        }
    }

    /**
     * Inicia la actividad de inicio de sesión.
     */
    private fun startLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    /**
     * Crea el menú de opciones de la actividad.
     *
     * @param menu Menú a inflar.
     * @return `true` si el menú fue creado correctamente.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_options, menu)
        return true
    }

    /**
     * Maneja las selecciones del menú de opciones.
     *
     * @param item Elemento seleccionado.
     * @return `true` si la acción fue manejada correctamente.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                performLogout()
                true
            }
            R.id.menu_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Realiza el cierre de sesión del usuario.
     */
    private fun performLogout() {
        lifecycleScope.launch {
            session.clearSession()
            Snackbar.make(binding.root, getString(R.string.logout_message), Snackbar.LENGTH_SHORT).show()
            startLogin()
        }
    }

    /**
     * Muestra un cuadro de diálogo con información sobre la aplicación.
     */
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.about_title))
            .setMessage(getString(R.string.about_message))
            .setPositiveButton(getString(R.string.accept_button), null)
            .show()
    }
}
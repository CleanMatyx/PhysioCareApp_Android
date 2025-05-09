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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val app by lazy { application as PhysioApp }
    private val session by lazy { app.sessionManager }

    private var currentRole: String? = null

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

    private fun checkSessionAndInitUI() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                session.sessionFlow.collect { data ->
                    val token = data?.token
                    val role = data?.role.orEmpty()
                    val username = data?.username.orEmpty()

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

    private fun updateBottomNavLabels(role: String) {
        val menu = binding.bottomNav.menu
        menu.clear()
        if (role == "physio") {
            menu.add(0, R.id.nav_consultations, 0, getString(R.string.menu_gestionar_citas))
                .setIcon(R.drawable.ic_appointments_icon)
            menu.add(0, R.id.nav_patients, 1, getString(R.string.menu_mi_perfil))
                .setIcon(R.drawable.ic_profile_physio_icon)
            menu.add(0, R.id.nav_physio_patients, 2, getString(R.string.menu_pacientes))
                .setIcon(R.drawable.ic_person)
        } else {
            menu.add(0, R.id.nav_consultations, 0, getString(R.string.menu_mis_consultas))
                .setIcon(R.drawable.ic_record_icon)
            menu.add(0, R.id.nav_patients, 1, getString(R.string.menu_mi_perfil))
                .setIcon(R.drawable.ic_user_icon)
        }
    }

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

    private fun showConsultations() {
        supportFragmentManager.commit {
            replace(binding.navHostContainer.id, ConsultationsFragment())
        }
    }

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

    private fun showPatientList() {
        supportFragmentManager.commit {
            replace(binding.navHostContainer.id, PatientsFragment())
        }
    }

    private fun startLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_options, menu)
        return true
    }

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

    private fun performLogout() {
        lifecycleScope.launch {
            session.clearSession()
            Snackbar.make(binding.root, getString(R.string.logout_message), Snackbar.LENGTH_SHORT).show()
            startLogin()
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Acerca de")
            .setMessage("Autor: Matías Exequiel Borra Quiroz\nCurso: DAM/DAW\nAño académico: 2024/2025")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
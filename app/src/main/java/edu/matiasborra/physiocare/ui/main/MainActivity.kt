package edu.matiasborra.physiocare.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.ActivityMainBinding
import edu.matiasborra.physiocare.ui.login.LoginActivity
import edu.matiasborra.physiocare.ui.main.consultations.ConsultationsFragment
import edu.matiasborra.physiocare.ui.main.patients.PatientsFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val app by lazy { application as PhysioApp }
    private val session by lazy { app.sessionManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        binding.tvUserInfo.isVisible = false

        setSupportActionBar(binding.toolbar)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        observeSession()
        checkSessionAndInitUI()
        setupBottomNav()
    }

    private fun observeSession() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                session.sessionFlow.collect { (token, username, userId, role) ->
                    if (!token.isNullOrEmpty()) {
                        binding.tvUserInfo.isVisible = true
                        binding.tvUserInfo.text = getString(
                            R.string.user_info,
                            username.orEmpty(),
                            userId.orEmpty(),
                            role.orEmpty()
                        )
                    }
                }
            }
        }
    }

    private fun checkSessionAndInitUI() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                session.getToken.collect { token ->
                    if (token.isNullOrEmpty()) {
                        startLogin()
                    } else {
                        showConsultations()
                    }
                }
            }
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
                else -> false
            }
        }
    }

    private fun showConsultations() {
        supportFragmentManager.commit {
            replace(binding.navHostContainer.id, ConsultationsFragment())
        }
        binding.toolbar.title = getString(R.string.consultations_title)
    }

    private fun showPatients() {
        supportFragmentManager.commit {
            replace(binding.navHostContainer.id, PatientsFragment())
        }
        binding.toolbar.title = getString(R.string.patients_title)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            session.clearSession()
            Snackbar
                .make(binding.root, getString(R.string.logout_message), Snackbar.LENGTH_SHORT)
                .show()
            startLogin()
        }
    }
}

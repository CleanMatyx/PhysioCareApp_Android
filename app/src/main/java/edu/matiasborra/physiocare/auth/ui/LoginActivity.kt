package edu.matiasborra.physiocare.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import edu.matiasborra.physiocare.PhysioApp
import edu.matiasborra.physiocare.auth.data.LoginState
import edu.matiasborra.physiocare.data.api.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository
import edu.matiasborra.physiocare.databinding.ActivityLoginBinding
import edu.matiasborra.physiocare.ui.main.MainActivity

/**
 * Actividad de inicio de sesión que permite a los usuarios autenticarse en la aplicación.
 * Gestiona la interacción con la interfaz de usuario y el ViewModel asociado.
 * 
 * @author Matias Borra
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val app by lazy { application as PhysioApp }
    private val session by lazy { app.sessionManager }

    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(
                    repo = PhysioRepository(RemoteDataSource(), session),
                    session = session
                ) as T
            }
        }
    }

    /**
     * Método llamado cuando se crea la actividad.
     * Configura el estado inicial de la actividad, incluyendo la observación del estado de la UI
     * y la configuración de los listeners.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val token = session.getToken.firstOrNull().orEmpty()
            if (token.isNotBlank()) {
                navigateToMain()
                return@launch
            }
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupListeners()
        observeUiState()
    }

    /**
     * Configura los listeners para los elementos de la interfaz de usuario.
     * En este caso, configura el botón de inicio de sesión para iniciar el proceso de autenticación.
     */
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val user = binding.etUsername.text.toString()
            val pass = binding.etPassword.text.toString()
            viewModel.login(user, pass)
        }
    }

    /**
     * Observa el estado de la interfaz de usuario (UI) del ViewModel.
     * Actualiza la interfaz de usuario según el estado actual (Idle, Loading, Success, Error).
     */
    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginState.Idle -> showLoading(false)
                        is LoginState.Loading -> showLoading(true)
                        is LoginState.Success -> {
                            showLoading(false)
                            navigateToMain()
                        }
                        is LoginState.Error -> {
                            showLoading(false)
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Muestra u oculta el indicador de carga en la interfaz de usuario.
     *
     * @param isLoading Indica si se debe mostrar el indicador de carga.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled  = !isLoading
    }

    /**
     * Navega a la actividad principal de la aplicación después de un inicio de sesión exitoso.
     * Finaliza la actividad actual.
     */
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

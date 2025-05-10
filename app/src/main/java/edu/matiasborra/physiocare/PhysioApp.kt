package edu.matiasborra.physiocare

import android.app.Application
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.utils.dataStore
import edu.matiasborra.physiocare.data.api.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository

/**
 * Clase principal de la aplicación.
 * Inicializa y proporciona acceso a las dependencias globales como el administrador de sesión
 * y el repositorio.
 *
 * @author Matias Borra
 */
class PhysioApp : Application() {

    /**
     * Administrador de sesión para manejar la autenticación y los datos del usuario.
     */
    val sessionManager by lazy { SessionManager(this.dataStore) }

    /**
     * Repositorio para acceder a los datos relacionados con fisioterapeutas y pacientes.
     */
    val physioRepo by lazy { PhysioRepository(RemoteDataSource(), sessionManager) }
}
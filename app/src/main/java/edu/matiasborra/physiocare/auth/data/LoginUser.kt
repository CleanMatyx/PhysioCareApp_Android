package edu.matiasborra.physiocare.auth.data

/**
 * Estado de la autenticación del usuario.
 * @author Matias Borra
 */
sealed class LoginState {
    /**
     * Estado inactivo de la autenticación.
     * @author Matias Borra
     */
    object Idle: LoginState()

    /**
     * Estado de carga de la autenticación.
     * @author Matias Borra
     */
    object Loading: LoginState()

    /**
     * Estado de éxito de la autenticación.
     * @property response Respuesta de la autenticación.
     * @author Matias Borra
     */
    data class Success(val response: LoginResponse): LoginState()

    /**
     * Estado de error de la autenticación.
     * @property message Mensaje de error.
     * @author Matias Borra
     */
    data class Error(val message: String): LoginState()
}

/**
 * Solicitud de inicio de sesión.
 * @property user Nombre de usuario.
 * @property password Contraseña del usuario.
 * @constructor Crea una instancia de LoginRequest.
 * @author Matias Borra
 */
data class LoginRequest(
    val login: String,
    val password: String
)

/**
 * Respuesta de inicio de sesión.
 * @property ok Indica si la autenticación fue exitosa.
 * @property token Token de autenticación.
 * @property error Mensaje de error en caso de fallo.
 * @constructor Crea una instancia de LoginResponse.
 * @author Matias Borra
 */
data class LoginResponse(
    val ok: Boolean,
    val token: String?,
    val userId: String?,
    val login: String?,
    val rol: String?,
    val error: String?
)
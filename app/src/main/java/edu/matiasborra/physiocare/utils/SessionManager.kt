package edu.matiasborra.physiocare.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Datos completos de la sesión de usuario.
 * Contiene información como token, nombre de usuario, ID y rol.
 *
 * @author Matias Borra
 */
data class SessionData(
    val token: String?,
    val username: String?,
    val userId: String?,
    val role: String?
)

/**
 * Clase para manejar la sesión del usuario utilizando DataStore.
 * Permite guardar, obtener y borrar datos de sesión.
 *
 * @author Matias Borra
 */
class SessionManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        /**
         * Clave para almacenar el token de autenticación.
         */
        private val TOKEN_KEY = stringPreferencesKey("token")

        /**
         * Clave para almacenar el nombre de usuario.
         */
        private val USERNAME_KEY = stringPreferencesKey("username")

        /**
         * Clave para almacenar el ID del usuario.
         */
        private val USER_ID_KEY = stringPreferencesKey("user_id")

        /**
         * Clave para almacenar el rol del usuario.
         */
        private val ROLE_KEY = stringPreferencesKey("role")
    }

    /**
     * Flujo que emite todos los datos de la sesión como un objeto [SessionData].
     */
    val sessionFlow: Flow<SessionData> = dataStore.data.map { prefs ->
        SessionData(
            token = prefs[TOKEN_KEY],
            username = prefs[USERNAME_KEY],
            userId = prefs[USER_ID_KEY],
            role = prefs[ROLE_KEY]
        )
    }

    /**
     * Guarda todos los datos de la sesión tras un inicio de sesión exitoso.
     *
     * @param token Token de autenticación.
     * @param username Nombre de usuario.
     * @param userId ID del usuario.
     * @param role Rol del usuario.
     */
    suspend fun saveSession(
        token: String,
        username: String,
        userId: String,
        role: String
    ) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USERNAME_KEY] = username
            prefs[USER_ID_KEY] = userId
            prefs[ROLE_KEY] = role
        }
    }

    /**
     * Borra todos los datos de la sesión almacenados.
     */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    /**
     * Flujo que emite únicamente el token de autenticación.
     */
    val getToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }

    /**
     * Flujo que emite únicamente el nombre de usuario.
     */
    val getUsername: Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }

    /**
     * Flujo que emite únicamente el ID del usuario.
     */
    val getUserId: Flow<String?> = dataStore.data.map { it[USER_ID_KEY] }
}
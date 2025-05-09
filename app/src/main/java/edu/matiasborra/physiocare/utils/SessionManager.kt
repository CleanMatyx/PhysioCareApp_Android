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
 */
data class SessionData(
    val token: String?,
    val username: String?,
    val userId: String?,
    val role: String?
)

/**
 * Clase para manejar la sesión del usuario utilizando DataStore.
 * Ahora almacena token, nombre de usuario, id y rol.
 */
class SessionManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val ROLE_KEY = stringPreferencesKey("role")
    }

    /**
     * Flujo que emite todos los datos de la sesión como [SessionData].
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
     * Guarda todos los datos de la sesión tras un login exitoso.
     * @param token   Token JWT o similar.
     * @param username Nombre de usuario.
     * @param userId   ID del usuario.
     * @param role     Rol del usuario ("physio", "patient", "admin", etc).
     */
    suspend fun saveSession(
        token: String,
        username: String,
        userId: String,
        role: String
    ) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY]    = token
            prefs[USERNAME_KEY] = username
            prefs[USER_ID_KEY]  = userId
            prefs[ROLE_KEY]     = role
        }
    }

    /**
     * Borra todos los datos de la sesión.
     */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    /** Flujo del token únicamente */
    val getToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    /** Flujo del nombre de usuario únicamente */
    val getUsername: Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }
    /** Flujo del ID de usuario únicamente */
    val getUserId: Flow<String?> = dataStore.data.map { it[USER_ID_KEY] }
}

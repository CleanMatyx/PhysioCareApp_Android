package edu.matiasborra.physiocare

import android.app.Application
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.auth.dataStore
import edu.matiasborra.physiocare.data.remote.PhysioApiClient

class PhysioApp : Application() {
    val sessionManager by lazy { SessionManager(this.dataStore) }

    override fun onCreate() {
        super.onCreate()
    }
}
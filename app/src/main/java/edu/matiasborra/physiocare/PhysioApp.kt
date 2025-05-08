package edu.matiasborra.physiocare

import android.app.Application
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.auth.dataStore
import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository

class PhysioApp : Application() {
    val sessionManager by lazy { SessionManager(this.dataStore) }
    val physioRepo by lazy { PhysioRepository(RemoteDataSource(), sessionManager) }
}

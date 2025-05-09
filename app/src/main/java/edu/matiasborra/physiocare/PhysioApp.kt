package edu.matiasborra.physiocare

import android.app.Application
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.utils.dataStore
import edu.matiasborra.physiocare.data.api.RemoteDataSource
import edu.matiasborra.physiocare.data.repository.PhysioRepository

class PhysioApp : Application() {
    val sessionManager by lazy { SessionManager(this.dataStore) }
    val physioRepo by lazy { PhysioRepository(RemoteDataSource(), sessionManager) }
}

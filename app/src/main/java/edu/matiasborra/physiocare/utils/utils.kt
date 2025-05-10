package edu.matiasborra.physiocare.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Utilidad para comprobar la conexión a internet en el dispositivo.
 * Proporciona métodos para verificar si el dispositivo está conectado a través de Wi-Fi, datos móviles o Ethernet.
 *
 * @author Matias Borra
 */

/**
 * Comprueba si hay conexión a internet en el dispositivo.
 *
 * @param context Contexto de la aplicación.
 * @return `true` si hay conexión, `false` si no.
 */
fun checkConnection(context: Context): Boolean {
    val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetwork
    if (networkInfo != null) {
        val activeNetwork = cm.getNetworkCapabilities(networkInfo)
        if (activeNetwork != null) {
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    return false
}
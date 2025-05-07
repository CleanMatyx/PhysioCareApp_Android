package edu.matiasborra.physiocare.data.remote.models

data class LoginResponse(
    val ok: Boolean,
    val token: String,
    val message: String?
)
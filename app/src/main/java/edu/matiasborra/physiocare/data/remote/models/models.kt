package edu.matiasborra.physiocare.data.remote.models

data class LoginRequest(val login: String, val password: String)
data class LoginResult(val token: String)
data class MessageResponse(val message: String)

data class ApiResponse<T>(
    val ok: Boolean,
    val result: T?,
    val message: String?
)

data class PatientItem(
    val _id: String,
    val name: String,
    val surname: String,
    val birthDate: String,
    val address: String?,
    val insuranceNumber: String,
    val email: String,
    val image: String?
)

data class PhysioItem(
    val _id: String,
    val name: String,
    val surname: String,
    val specialty: String,
    val licenseNumber: String,
    val email: String,
    val image: String?
)

data class AppointmentItem(
    val date: String,
    val physio: PhysioBrief?,
    val diagnosis: String,
    val treatment: String,
    val observations: String?,
    val _id: String
)

data class RecordItem(
    val _id: String,
    val patient: String,
    val medicalRecord: String?,
    val appointments: List<AppointmentItem>
)

data class AppointmentFlat(
    val patientName: String,
    val physioName: String,
    val date: String,
    val diagnosis: String,
    val treatment: String,
    val observations: String?
)

data class AppointmentRequest(
    val date: String,
    val physio: String,
    val diagnosis: String,
    val treatment: String,
    val observations: String?
)

package edu.matiasborra.physiocare.data.remote.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

data class PatientDetailResponse(
    val patient: PatientItem,
    val records: List<RecordItem>
)

data class PhysioItem(
    val id: String,
    val name: String,
    val surname: String,
    val specialty: String,
    val licenseNumber: String,
    val email: String,
    val image: String?
)

//data class AppointmentItem(
//    val date: String,
//    val physio: PhysioBrief?,
//    val diagnosis: String,
//    val treatment: String,
//    val observations: String?,
//    val _id: String
//)

data class AppointmentItem(
    val _id: String,                // añade el id de la cita
    val date: String,
    val physio: AppointmentPhysio?, // antes era String, ahora este objeto
    val diagnosis: String,
    val treatment: String,
    val observations: String?
)

data class AppointmentPhysio(
    val _id: String,
    val name: String,
    val surname: String
)

data class RecordItem(
    val _id: String,
    val patient: String,
    val medicalRecord: String?,
    val appointments: List<AppointmentItem>
)


data class AppointmentFlat(
//    @SerializedName("appointmentId")
    val id: String,
    val patientName: String,
    val physioName: String,
    val physioId: String?,
    val date: String,
    val diagnosis: String,
    val treatment: String,
    val observations: String
) : Serializable

data class AppointmentRequest(
    val date: String,
    val physio: String,
    val diagnosis: String,
    val treatment: String,
    val observations: String?
)

data class PhysioBrief(
    val _id: String,
    val name: String,
    val surname: String
)

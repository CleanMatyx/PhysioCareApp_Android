package edu.matiasborra.physiocare.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Solicitud de inicio de sesión.
 * @property login Nombre de usuario.
 * @property password Contraseña.
 * @author Matias Borra
 */
data class LoginRequest(val login: String, val password: String)

/**
 * Respuesta con mensaje simple.
 * @property message Mensaje de la respuesta.
 * @author Matias Borra
 */
data class MessageResponse(val message: String)

/**
 * Respuesta genérica de la API.
 * @param T Tipo de resultado.
 * @property ok Indica si la operación fue exitosa.
 * @property result Resultado de la operación.
 * @property message Mensaje adicional.
 * @author Matias Borra
 */
data class ApiResponse<T>(
    val ok: Boolean,
    val result: T?,
    val message: String?
)

/**
 * Datos de un paciente.
 * @property _id ID del paciente.
 * @property name Nombre.
 * @property surname Apellido.
 * @property birthDate Fecha de nacimiento.
 * @property address Dirección.
 * @property insuranceNumber Número de seguro.
 * @property email Correo electrónico.
 * @property image Imagen de perfil.
 * @author Matias Borra
 */
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

/**
 * Detalle de un paciente, incluyendo sus expedientes.
 * @property patient Datos del paciente.
 * @property records Lista de expedientes.
 * @author Matias Borra
 */
data class PatientDetailResponse(
    val patient: PatientItem,
    val records: List<RecordItem>
)

/**
 * Datos de un fisioterapeuta.
 * @property _id ID del fisioterapeuta.
 * @property name Nombre.
 * @property surname Apellido.
 * @property specialty Especialidad.
 * @property licenseNumber Número de licencia.
 * @property email Correo electrónico.
 * @property image Imagen de perfil.
 * @author Matias Borra
 */
data class PhysioItem(
    @SerializedName("_id")
    val _id: String,
    val name: String,
    val surname: String,
    val specialty: String,
    val licenseNumber: String,
    val email: String,
    val image: String? = null
)

/**
 * Datos de una cita.
 * @property _id ID de la cita.
 * @property date Fecha de la cita.
 * @property physio Fisioterapeuta asignado.
 * @property diagnosis Diagnóstico.
 * @property treatment Tratamiento.
 * @property observations Observaciones.
 * @author Matias Borra
 */
data class AppointmentItem(
    val _id: String,
    val date: String,
    val physio: AppointmentPhysio?,
    val diagnosis: String,
    val treatment: String,
    val observations: String?
)

/**
 * Información breve de un fisioterapeuta en una cita.
 * @property _id ID del fisioterapeuta.
 * @property name Nombre.
 * @property surname Apellido.
 * @author Matias Borra
 */
data class AppointmentPhysio(
    val _id: String,
    val name: String,
    val surname: String
)

/**
 * Expediente médico de un paciente.
 * @property _id ID del expediente.
 * @property patient ID del paciente.
 * @property medicalRecord Información médica.
 * @property appointments Lista de citas.
 * @author Matias Borra
 */
data class RecordItem(
    val _id: String,
    val patient: String,
    val medicalRecord: String?,
    val appointments: List<AppointmentItem>
)

/**
 * Cita médica en formato plano.
 * @property id ID de la cita.
 * @property patientName Nombre del paciente.
 * @property physioName Nombre del fisioterapeuta.
 * @property physioId ID del fisioterapeuta.
 * @property date Fecha de la cita.
 * @property diagnosis Diagnóstico.
 * @property treatment Tratamiento.
 * @property observations Observaciones.
 * @author Matias Borra
 */
data class AppointmentFlat(
    val id: String,
    val patientName: String,
    val physioName: String,
    val physioId: String?,
    val date: String,
    val diagnosis: String,
    val treatment: String,
    val observations: String
) : Serializable

/**
 * Solicitud para crear una cita.
 * @property date Fecha de la cita.
 * @property physio ID del fisioterapeuta.
 * @property diagnosis Diagnóstico.
 * @property treatment Tratamiento.
 * @property observations Observaciones.
 * @author Matias Borra
 */
data class AppointmentRequest(
    val date: String,
    val physio: String,
    val diagnosis: String,
    val treatment: String,
    val observations: String?
)
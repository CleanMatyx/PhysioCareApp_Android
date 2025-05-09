package edu.matiasborra.physiocare.data.api

import edu.matiasborra.physiocare.auth.data.LoginResponse
import edu.matiasborra.physiocare.data.models.ApiResponse
import edu.matiasborra.physiocare.data.models.AppointmentFlat
import edu.matiasborra.physiocare.data.models.AppointmentRequest
import edu.matiasborra.physiocare.data.models.LoginRequest
import edu.matiasborra.physiocare.data.models.MessageResponse
import edu.matiasborra.physiocare.data.models.PatientDetailResponse
import edu.matiasborra.physiocare.data.models.PatientItem
import edu.matiasborra.physiocare.data.models.PhysioItem
import edu.matiasborra.physiocare.data.models.RecordItem

/**
 * Fuente de datos remota para acceder a la API de PhysioCare.
 * Proporciona métodos para autenticación, gestión de pacientes, fisioterapeutas,
 * expedientes y citas.
 *
 * @constructor Crea una instancia de RemoteDataSource.
 * @author Matias Borra
 */
class RemoteDataSource {

    /**
     * Instancia perezosa de la interfaz de la API.
     */
    private val api: PhysioApiService by lazy {
        PhysioApiClient.getRetrofit2Api()
    }

    /**
     * Inicia sesión con las credenciales proporcionadas.
     *
     * @param login Nombre de usuario.
     * @param password Contraseña.
     * @return LoginResponse con el resultado de la autenticación.
     * @author Matias Borra
     */
    suspend fun login(login: String, password: String): LoginResponse =
        api.login(LoginRequest(login, password))

    /**
     * Obtiene la lista de todos los pacientes.
     *
     * @param token Token de autenticación.
     * @return ApiResponse con la lista de pacientes.
     * @author Matias Borra
     */
    suspend fun fetchAllPatients(token: String): ApiResponse<List<PatientItem>> =
        api.getPatients("Bearer $token")


    /**
     * Obtiene un paciente por su ID.
     *
     * @param token Token de autenticación.
     * @param id ID del paciente.
     * @return ApiResponse con el paciente encontrado.
     * @author Matias Borra
     */
    suspend fun fetchPatientById(token: String, id: String): ApiResponse<PatientItem> =
        api.getPatient("Bearer $token", id)

    /**
     * Obtiene el detalle de un paciente, incluyendo sus expedientes.
     *
     * @param token Token de autenticación.
     * @param id ID del paciente.
     * @return ApiResponse con el detalle del paciente.
     * @author Matias Borra
     */
    suspend fun getPatientDetail(token: String, id: String): ApiResponse<PatientDetailResponse> =
        api.getPatientDetail("Bearer $token", id)

    /**
     * Obtiene un fisioterapeuta por su ID.
     *
     * @param token Token de autenticación.
     * @param id ID del fisioterapeuta.
     * @return ApiResponse con el fisioterapeuta encontrado.
     * @author Matias Borra
     */
    suspend fun fetchPhysioById(token: String, id: String): ApiResponse<PhysioItem> =
        api.getPhysio("Bearer $token", id)

    /**
     * Añade una cita a un expediente.
     *
     * @param token Token de autenticación.
     * @param recordId ID del expediente.
     * @param req Objeto AppointmentRequest con los datos de la cita.
     * @return ApiResponse con el expediente actualizado.
     * @author Matias Borra
     */
    suspend fun addAppointment(token: String, recordId: String, req: AppointmentRequest) =
        api.addAppointment("Bearer $token", recordId, req)

    /**
     * Obtiene todas las citas de un paciente específico.
     *
     * @param token Token de autenticación.
     * @param patientId ID del paciente.
     * @return ApiResponse con la lista de citas del paciente.
     * @author Matias Borra
     */
    suspend fun getMyAppointments(
        token: String,
        patientId: String
    ): ApiResponse<List<AppointmentFlat>> {
        val resp = api.getPatientDetail("Bearer $token", patientId)
        return if (resp.ok && resp.result != null) {
            val allApps = resp.result.records
                .flatMap { it.appointments }
                .map { app ->
                    AppointmentFlat(
                        id = app._id,
                        patientName = "${resp.result.patient.name} ${resp.result.patient.surname}",
                        physioName = app.physio
                            ?.let { "${it.name} ${it.surname}" }
                            .orEmpty(),
                        date = app.date,
                        diagnosis = app.diagnosis,
                        treatment = app.treatment,
                        observations = app.observations.toString(),
                        physioId = app.physio?._id
                    )
                }
            ApiResponse(ok = true, result = allApps, message = null)
        } else {
            ApiResponse(ok = false, result = null, message = resp.message)
        }
    }

    /**
     * Obtiene todas las citas asociadas a un fisioterapeuta.
     *
     * @param token Token de autenticación.
     * @param physioId ID del fisioterapeuta.
     * @return ApiResponse con la lista de citas del fisioterapeuta.
     * @author Matias Borra
     */
    suspend fun getAppointmentsByPhysio(token: String, physioId: String): ApiResponse<List<AppointmentFlat>> =
        api.getAppointmentsByPhysio("Bearer $token", physioId)

    /**
     * Elimina una cita por su ID.
     *
     * @param token Token de autenticación.
     * @param appointmentId ID de la cita.
     * @return ApiResponse con el resultado de la eliminación.
     * @author Matias Borra
     */
    suspend fun deleteAppointment(token: String, appointmentId: String): ApiResponse<MessageResponse> =
        api.deleteAppointment("Bearer $token", appointmentId)

}
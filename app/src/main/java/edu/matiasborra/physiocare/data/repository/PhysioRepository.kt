package edu.matiasborra.physiocare.data.repository

import edu.matiasborra.physiocare.auth.data.LoginResponse
import edu.matiasborra.physiocare.utils.SessionManager
import edu.matiasborra.physiocare.data.api.RemoteDataSource
import edu.matiasborra.physiocare.data.models.ApiResponse
import edu.matiasborra.physiocare.data.models.AppointmentFlat
import edu.matiasborra.physiocare.data.models.AppointmentRequest
import edu.matiasborra.physiocare.data.models.MessageResponse
import edu.matiasborra.physiocare.data.models.PatientDetailResponse
import edu.matiasborra.physiocare.data.models.PatientItem
import edu.matiasborra.physiocare.data.models.PhysioItem
import edu.matiasborra.physiocare.data.models.RecordItem
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repositorio que gestiona la lógica de acceso a datos remotos y sesión para la aplicación PhysioCare.
 *
 * @property remote Fuente de datos remota.
 * @property sessionManager Gestor de sesión.
 * @author Matias Borra
 */
class PhysioRepository(
    private val remote: RemoteDataSource,
    private val sessionManager: SessionManager
) {

    /**
     * Inicia sesión con las credenciales proporcionadas.
     * @param login Nombre de usuario.
     * @param password Contraseña.
     * @return LoginResponse con el resultado de la autenticación.
     * @author Matias Borra
     */
    suspend fun login(login: String, password: String): LoginResponse =
        remote.login(login, password)

    /**
     * Obtiene la lista de todos los pacientes.
     * @param token Token de autenticación.
     * @return ApiResponse con la lista de pacientes.
     * @author Matias Borra
     */
    suspend fun getPatients(token: String): ApiResponse<List<PatientItem>> =
        remote.fetchAllPatients(token)

    /**
     * Obtiene un paciente por su ID.
     * @param token Token de autenticación.
     * @param id ID del paciente.
     * @return ApiResponse con el paciente encontrado.
     * @author Matias Borra
     */
    suspend fun getPatient(token: String, id: String): ApiResponse<PatientItem> =
        remote.fetchPatientById(token, id)

    /**
     * Obtiene el detalle de un paciente, incluyendo sus expedientes.
     * @param token Token de autenticación.
     * @param patientId ID del paciente.
     * @return ApiResponse con el detalle del paciente.
     * @author Matias Borra
     */
    suspend fun getPatientDetail(token: String, patientId: String): ApiResponse<PatientDetailResponse> =
        remote.getPatientDetail(token, patientId)

    /**
     * Obtiene la lista de todos los pacientes usando el token de la sesión.
     * @return Lista de pacientes.
     * @throws Exception si el token es inválido o hay error en la carga.
     * @author Matias Borra
     */
    suspend fun getAllPatients(): List<PatientItem> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        val response = remote.fetchAllPatients(token)
        if (response.ok && response.result != null) {
            return response.result
        } else {
            throw Exception(response.message ?: "Error al cargar pacientes")
        }
    }

    /**
     * Crea una cita para un paciente específico.
     * @param patientId ID del paciente.
     * @param date Fecha de la cita.
     * @param diagnosis Diagnóstico.
     * @param treatment Tratamiento.
     * @param observations Observaciones.
     * @return ApiResponse con el expediente actualizado.
     * @throws Exception si el token o el ID de physio no están disponibles.
     * @author Matias Borra
     */
    suspend fun createAppointmentForPatient(
        patientId: String,
        date: String,
        diagnosis: String,
        treatment: String,
        observations: String
    ): ApiResponse<RecordItem> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        val physioId = sessionManager.sessionFlow.firstOrNull()?.userId ?: throw Exception("ID de physio no disponible")

        val recordResponse = remote.getPatientDetail(token, patientId)
        val recordId = recordResponse.result?.records?.firstOrNull()?._id
            ?: throw Exception("No se encontró un expediente para este paciente")

        val req = AppointmentRequest(
            date = date,
            diagnosis = diagnosis,
            treatment = treatment,
            observations = observations,
            physio = physioId
        )
        return remote.addAppointment(token, patientId, req)
    }

    /**
     * Obtiene un fisioterapeuta por su ID.
     * @param token Token de autenticación.
     * @param id ID del fisioterapeuta.
     * @return ApiResponse con el fisioterapeuta encontrado.
     * @author Matias Borra
     */
    suspend fun getPhysio(token: String, id: String): ApiResponse<PhysioItem> =
        remote.fetchPhysioById(token, id)

    /**
     * Obtiene todas las citas de un paciente específico.
     * @param token Token de autenticación.
     * @param patientId ID del paciente.
     * @return ApiResponse con la lista de citas del paciente.
     * @author Matias Borra
     */
    suspend fun getMyAppointments(token: String, patientId: String)
            = remote.getMyAppointments(token, patientId)

    /**
     * Obtiene todas las citas asociadas al fisioterapeuta autenticado.
     * @return ApiResponse con la lista de citas del fisioterapeuta.
     * @throws Exception si el token o el ID no están disponibles.
     * @author Matias Borra
     */
    suspend fun getMyAppointmentsAsPhysio(): ApiResponse<List<AppointmentFlat>> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        val physioId = sessionManager.sessionFlow.firstOrNull()?.userId ?: throw Exception("ID no disponible")
        return remote.getAppointmentsByPhysio(token, physioId)
    }

    /**
     * Elimina una cita por su ID.
     * @param appointmentId ID de la cita.
     * @return ApiResponse con el resultado de la eliminación.
     * @throws Exception si el token es inválido.
     * @author Matias Borra
     */
    suspend fun deleteAppointment(appointmentId: String): ApiResponse<MessageResponse> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        return remote.deleteAppointment(token, appointmentId)
    }
}
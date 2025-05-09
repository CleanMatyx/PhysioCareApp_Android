package edu.matiasborra.physiocare.data.repository

import edu.matiasborra.physiocare.auth.LoginResponse
import edu.matiasborra.physiocare.auth.SessionManager
import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.remote.models.*
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

class PhysioRepository(
    private val remote: RemoteDataSource,
    private val sessionManager: SessionManager
) {

    // --- Autenticación ---
    suspend fun login(login: String, password: String): LoginResponse =
        remote.login(login, password)

    suspend fun logout(token: String): ApiResponse<MessageResponse> =
        remote.logout(token)

    // --- Pacientes ---
    suspend fun getPatients(token: String): ApiResponse<List<PatientItem>> =
        remote.fetchAllPatients(token)

    suspend fun findPatients(
        token: String,
        name: String?,
        surname: String?
    ): ApiResponse<List<PatientItem>> =
        remote.searchPatients(token, name, surname)

    suspend fun getPatient(token: String, id: String): ApiResponse<PatientItem> =
        remote.fetchPatientById(token, id)

    suspend fun getPatientDetail(token: String, patientId: String): ApiResponse<PatientDetailResponse> =
        remote.getPatientDetail(token, patientId)

    suspend fun getAllPatients(): List<PatientItem> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        val response = remote.fetchAllPatients(token)
        if (response.ok && response.result != null) {
            return response.result
        } else {
            throw Exception(response.message ?: "Error al cargar pacientes")
        }
    }

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

    suspend fun createPatient(
        token: String,
        patient: PatientItem
    ): ApiResponse<PatientItem> =
        remote.createPatient(token, patient)

    suspend fun updatePatient(
        token: String,
        id: String,
        patient: PatientItem
    ): ApiResponse<PatientItem> =
        remote.updatePatient(token, id, patient)

    suspend fun deletePatient(token: String, id: String): ApiResponse<PatientItem> =
        remote.deletePatient(token, id)

    // --- Fisioterapeutas ---
    suspend fun getPhysios(token: String): ApiResponse<List<PhysioItem>> =
        remote.fetchAllPhysios(token)

    suspend fun findPhysios(
        token: String,
        specialty: String?
    ): ApiResponse<List<PhysioItem>> =
        remote.searchPhysios(token, specialty)

    suspend fun getPhysio(token: String, id: String): ApiResponse<PhysioItem> =
        remote.fetchPhysioById(token, id)

    suspend fun createPhysio(
        token: String,
        physio: PhysioItem
    ): ApiResponse<PhysioItem> =
        remote.createPhysio(token, physio)

    suspend fun updatePhysio(
        token: String,
        id: String,
        physio: PhysioItem
    ): ApiResponse<PhysioItem> =
        remote.updatePhysio(token, id, physio)

    suspend fun deletePhysio(token: String, id: String): ApiResponse<PhysioItem> =
        remote.deletePhysio(token, id)

    // --- Expedientes y Citas ---
    suspend fun getRecords(token: String): ApiResponse<List<RecordItem>> =
        remote.fetchRecords(token)

    suspend fun getAppointments(token: String): ApiResponse<List<AppointmentFlat>> =
        remote.fetchAppointments(token)

    suspend fun getRecord(token: String, id: String): ApiResponse<RecordItem> =
        remote.fetchRecordById(token, id)

    suspend fun createRecord(
        token: String,
        record: RecordItem
    ): ApiResponse<RecordItem> =
        remote.createRecord(token, record)

    suspend fun addAppointment(token: String, recordId: String, req: AppointmentRequest) =
        remote.addAppointment(token, recordId, req)

    suspend fun getAppointmentDetail(token: String, appointmentId: String)
            = remote.getAppointmentDetail(token, appointmentId)

    suspend fun getMyAppointments(token: String, patientId: String)
            = remote.getMyAppointments(token, patientId)

    /** Sólo para admin/physio: todas las citas */
    suspend fun getAllAppointments(token: String)
            = remote.getAppointments(token)

    suspend fun getMyAppointmentsAsPhysio(): ApiResponse<List<AppointmentFlat>> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        val physioId = sessionManager.sessionFlow.firstOrNull()?.userId ?: throw Exception("ID no disponible")
        return remote.getAppointmentsByPhysio(token, physioId)
    }

    suspend fun deleteAppointment(appointmentId: String): ApiResponse<MessageResponse> {
        val token = sessionManager.getToken.firstOrNull() ?: throw Exception("Token inválido")
        return remote.deleteAppointment(token, appointmentId)
    }


}

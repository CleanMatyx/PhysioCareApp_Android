package edu.matiasborra.physiocare.data.repository

import edu.matiasborra.physiocare.data.remote.RemoteDataSource
import edu.matiasborra.physiocare.data.remote.models.*

class PhysioRepository(
    private val remote: RemoteDataSource
) {

    // --- Autenticación ---
    suspend fun login(login: String, password: String): ApiResponse<LoginResult> =
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

    suspend fun getPatientDetail(token: String, id: String): ApiResponse<PatientDetailResponse> =
        remote.getPatientDetail(token, id)


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

    suspend fun addAppointment(
        token: String,
        recordId: String,
        request: AppointmentRequest
    ): ApiResponse<RecordItem> =
        remote.addAppointment(token, recordId, request)
}

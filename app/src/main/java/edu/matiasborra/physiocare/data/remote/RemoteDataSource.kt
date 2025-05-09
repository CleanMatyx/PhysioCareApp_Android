package edu.matiasborra.physiocare.data.remote

import android.content.Context
import edu.matiasborra.physiocare.auth.LoginResponse
import edu.matiasborra.physiocare.data.remote.models.*

/**
 * Fuente de datos remota para acceder a la API de PhysioCare.
 * @constructor Crea una instancia de RemoteDataSource.
 * @author Matias Borra
 */
class RemoteDataSource{

    private val api: PhysioApiService by lazy {
        PhysioApiClient.getRetrofit2Api()
    }

    // --- Autenticación ---
    suspend fun login(login: String, password: String): LoginResponse =
        api.login(LoginRequest(login, password))


    suspend fun logout(token: String): ApiResponse<MessageResponse> =
        api.logout("Bearer $token")

    // --- Pacientes ---
    suspend fun fetchAllPatients(token: String): ApiResponse<List<PatientItem>> =
        api.getPatients("Bearer $token")

    suspend fun searchPatients(
        token: String,
        name: String?,
        surname: String?
    ): ApiResponse<List<PatientItem>> =
        api.findPatients("Bearer $token", name, surname)

    suspend fun fetchPatientById(token: String, id: String): ApiResponse<PatientItem> =
        api.getPatient("Bearer $token", id)

    /** Trae paciente + records en un sólo call */
    suspend fun getPatientDetail(token: String, id: String): ApiResponse<PatientDetailResponse> =
        api.getPatientDetail("Bearer $token", id)

    suspend fun createPatient(
        token: String,
        newPatient: PatientItem
    ): ApiResponse<PatientItem> =
        api.createPatient("Bearer $token", newPatient)

    suspend fun updatePatient(
        token: String,
        id: String,
        updatedPatient: PatientItem
    ): ApiResponse<PatientItem> =
        api.updatePatient("Bearer $token", id, updatedPatient)

    suspend fun deletePatient(token: String, id: String): ApiResponse<PatientItem> =
        api.deletePatient("Bearer $token", id)

    // --- Fisioterapeutas ---
    suspend fun fetchAllPhysios(token: String): ApiResponse<List<PhysioItem>> =
        api.getPhysios("Bearer $token")

    suspend fun searchPhysios(
        token: String,
        specialty: String?
    ): ApiResponse<List<PhysioItem>> =
        api.findPhysios("Bearer $token", specialty)

    suspend fun fetchPhysioById(token: String, id: String): ApiResponse<PhysioItem> =
        api.getPhysio("Bearer $token", id)

    suspend fun createPhysio(
        token: String,
        newPhysio: PhysioItem
    ): ApiResponse<PhysioItem> =
        api.createPhysio("Bearer $token", newPhysio)

    suspend fun updatePhysio(
        token: String,
        id: String,
        updatedPhysio: PhysioItem
    ): ApiResponse<PhysioItem> =
        api.updatePhysio("Bearer $token", id, updatedPhysio)

    suspend fun deletePhysio(token: String, id: String): ApiResponse<PhysioItem> =
        api.deletePhysio("Bearer $token", id)

    // --- Expedientes y Citas ---
    suspend fun fetchRecords(token: String): ApiResponse<List<RecordItem>> =
        api.getRecords("Bearer $token")

    suspend fun fetchAppointments(token: String): ApiResponse<List<AppointmentFlat>> =
        api.getAppointments("Bearer $token")

    suspend fun fetchRecordById(token: String, id: String): ApiResponse<RecordItem> =
        api.getRecord("Bearer $token", id)

    suspend fun createRecord(
        token: String,
        newRecord: RecordItem
    ): ApiResponse<RecordItem> =
        api.createRecord("Bearer $token", newRecord)

//    suspend fun addAppointment(
//        token: String,
//        recordId: String,
//        newApp: AppointmentRequest
//    ): ApiResponse<RecordItem> =
//        api.addAppointment("Bearer $token", recordId, newApp

    suspend fun addAppointment(token: String, recordId: String, req: AppointmentRequest) =
        api.addAppointment("Bearer $token", recordId, req)

    /** Trae detalle de cita por su id */
//    suspend fun getAppointmentDetail(token: String, appointmentId: String)
//            : ApiResponse<AppointmentItem> {
//        return api.getAppointmentDetail("Bearer $token", appointmentId)
//    }

    suspend fun getAppointmentDetail(token: String, id: String) =
        api.getAppointmentDetail("Bearer $token", id)

    /**
     * Para pacientes: extrae sólo sus citas de dentro de PatientDetailResponse.
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

    /** Para admin/physio: trae todas las citas de todos los pacientes */
    suspend fun getAppointments(token: String): ApiResponse<List<AppointmentFlat>> {
        val resp = api.getAppointments("Bearer $token")
        return if (resp.ok && resp.result != null) {
            ApiResponse(ok = true, result = resp.result, message = null)
        } else {
            ApiResponse(ok = false, result = null, message = resp.message)
        }
    }

    suspend fun getAppointmentsByPhysio(token: String, physioId: String): ApiResponse<List<AppointmentFlat>> =
        api.getAppointmentsByPhysio("Bearer $token", physioId)

    suspend fun deleteAppointment(token: String, appointmentId: String): ApiResponse<MessageResponse> =
        api.deleteAppointment("Bearer $token", appointmentId)



}
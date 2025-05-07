package edu.matiasborra.physiocare.data.remote

import edu.matiasborra.physiocare.auth.LoginResponse
import edu.matiasborra.physiocare.data.remote.models.*
import retrofit2.Retrofit
import retrofit2.http.*
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Clase que proporciona la configuración de Retrofit para acceder a la API de Physiocare.
 * @author Matias Borra
 */
class PhysioApiClient {
    companion object {
        private const val BASE_URL = "https://matiasborra.es/api/physio/"

        /**
         * Obtiene una instancia de CoffeeAPIInterface configurada con Retrofit.
         * @return CoffeeAPIInterface Instancia de la interfaz de la API.
         * @author Matias Borra
         */
        fun getRetrofit2Api(): PhysioApiService {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(PhysioApiService::class.java)
        }
    }
}


/**
 * Interfaz que define los métodos para acceder a la API de PhysioCare.
 * @author Matias Borra
 */
interface PhysioApiService {
    @POST("auth/login")
    suspend fun login(@Body creds: LoginRequest): LoginResponse

    @GET("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): ApiResponse<MessageResponse>

    @GET("patients")
    suspend fun getPatients(@Header("Authorization") token: String): ApiResponse<List<PatientItem>>

    @GET("patients/find")
    suspend fun findPatients(
        @Header("Authorization") token: String,
        @Query("name") name: String?,
        @Query("surname") surname: String?
    ): ApiResponse<List<PatientItem>>

    @GET("patients/{id}")
    suspend fun getPatient(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<PatientItem>

    @POST("patients")
    suspend fun createPatient(
        @Header("Authorization") token: String,
        @Body newPatient: PatientItem
    ): ApiResponse<PatientItem>

    @PUT("patients/{id}")
    suspend fun updatePatient(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body updatedPatient: PatientItem
    ): ApiResponse<PatientItem>

    @DELETE("patients/{id}")
    suspend fun deletePatient(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<PatientItem>

    @GET("physios")
    suspend fun getPhysios(@Header("Authorization") token: String): ApiResponse<List<PhysioItem>>

    @GET("physios/find")
    suspend fun findPhysios(
        @Header("Authorization") token: String,
        @Query("specialty") specialty: String?
    ): ApiResponse<List<PhysioItem>>

    @GET("physios/{id}")
    suspend fun getPhysio(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<PhysioItem>

    @POST("physios")
    suspend fun createPhysio(
        @Header("Authorization") token: String,
        @Body newPhysio: PhysioItem
    ): ApiResponse<PhysioItem>

    @PUT("physios/{id}")
    suspend fun updatePhysio(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body updatedPhysio: PhysioItem
    ): ApiResponse<PhysioItem>

    @DELETE("physios/{id}")
    suspend fun deletePhysio(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<PhysioItem>

    @GET("records")
    suspend fun getRecords(@Header("Authorization") token: String): ApiResponse<List<RecordItem>>

    @GET("records/appointments")
    suspend fun getAppointments(@Header("Authorization") token: String): ApiResponse<List<AppointmentFlat>>

    @GET("records/{id}")
    suspend fun getRecord(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<RecordItem>

    @POST("records")
    suspend fun createRecord(
        @Header("Authorization") token: String,
        @Body newRecord: RecordItem
    ): ApiResponse<RecordItem>

    @POST("records/{id}/appointments")
    suspend fun addAppointment(
        @Header("Authorization") token: String,
        @Path("id") recordId: String,
        @Body newApp: AppointmentRequest
    ): ApiResponse<RecordItem>
}
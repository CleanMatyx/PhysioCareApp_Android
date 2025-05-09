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
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Clase que proporciona la configuración de Retrofit para acceder a la API de Physiocare.
 * Permite obtener una instancia de la interfaz de servicios de la API.
 *
 * @author Matias Borra
 */
class PhysioApiClient {
    companion object {
        private const val BASE_URL = "https://matiasborra.es/api/physio/"

        /**
         * Obtiene una instancia de PhysioApiService configurada con Retrofit.
         *
         * @return Instancia de PhysioApiService.
         * @author Matias Borra
         */
        fun getRetrofit2Api(): PhysioApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhysioApiService::class.java)
        }
    }
}

/**
 * Interfaz que define los métodos para acceder a la API de PhysioCare.
 *
 * @author Matias Borra
 */
interface PhysioApiService {
    /**
     * Inicia sesión con las credenciales proporcionadas.
     * @param request Objeto LoginRequest con los datos de acceso.
     * @return LoginResponse con el resultado de la autenticación.
     * @author Matias Borra
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * Obtiene la lista de todos los pacientes.
     * @param token Token de autenticación.
     * @return ApiResponse con la lista de pacientes.
     * @author Matias Borra
     */
    @GET("patients")
    suspend fun getPatients(@Header("Authorization") token: String): ApiResponse<List<PatientItem>>

    /**
     * Obtiene un paciente por su ID.
     * @param token Token de autenticación.
     * @param id ID del paciente.
     * @return ApiResponse con el paciente encontrado.
     * @author Matias Borra
     */
    @GET("patients/{id}")
    suspend fun getPatient(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<PatientItem>

    /**
     * Devuelve el detalle de un paciente, incluyendo sus expedientes.
     * @param auth Token de autenticación.
     * @param patientId ID del paciente.
     * @return ApiResponse con el detalle del paciente.
     * @author Matias Borra
     */
    @GET("patients/{id}")
    suspend fun getPatientDetail(
        @Header("Authorization") auth: String,
        @Path("id") patientId: String
    ): ApiResponse<PatientDetailResponse>

    /**
     * Obtiene un fisioterapeuta por su ID.
     * @param token Token de autenticación.
     * @param id ID del fisioterapeuta.
     * @return ApiResponse con el fisioterapeuta encontrado.
     * @author Matias Borra
     */
    @GET("physios/{id}")
    suspend fun getPhysio(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<PhysioItem>

    /**
     * Añade una cita a un expediente.
     * @param token Token de autenticación.
     * @param recordId ID del expediente.
     * @param newApp Objeto AppointmentRequest con los datos de la cita.
     * @return ApiResponse con el expediente actualizado.
     * @author Matias Borra
     */
    @POST("records/patients/{id}/appointments")
    suspend fun addAppointment(
        @Header("Authorization") token: String,
        @Path("id") recordId: String,
        @Body newApp: AppointmentRequest
    ): ApiResponse<RecordItem>

    /**
     * Obtiene todas las citas asociadas a un fisioterapeuta.
     * @param token Token de autenticación.
     * @param physioId ID del fisioterapeuta.
     * @return ApiResponse con la lista de citas del fisioterapeuta.
     * @author Matias Borra
     */
    @GET("records/physio/{id}/appointments")
    suspend fun getAppointmentsByPhysio(
        @Header("Authorization") token: String,
        @Path("id") physioId: String
    ): ApiResponse<List<AppointmentFlat>>

    /**
     * Elimina una cita por su ID.
     * @param token Token de autenticación.
     * @param appointmentId ID de la cita.
     * @return ApiResponse con el resultado de la eliminación.
     * @author Matias Borra
     */
    @DELETE("records/appointments/{id}")
    suspend fun deleteAppointment(
        @Header("Authorization") token: String,
        @Path("id") appointmentId: String
    ): ApiResponse<MessageResponse>
}
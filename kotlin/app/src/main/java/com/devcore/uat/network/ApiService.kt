package com.devcore.uat.network

import retrofit2.Response
import retrofit2.http.*

// --- Auth / Login ---
data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val access_token: String, val token_type: String)

// --- Usuarios ---
data class UsuarioCreate(val nombre: String, val email: String, val password: String)
data class UsuarioResponse(
    val id: Int?,
    val nombre: String?,
    val email: String?,
    val nivel_confianza: Int?,
    val racha_diaria: Int?,
    val ultima_conexion: String?,
    val correo_recuperacion: String?,
    val huella_habilitada: Boolean?
)

// --- Actualización de perfil (PATCH parcial) ---
data class UsuarioUpdateRequest(
    val correo_recuperacion: String? = null,
    val huella_habilitada: Boolean? = null,
    val semestre: Int? = null,
    val grupo: String? = null
)

// --- Recuperación de contraseña ---
data class ForgotPasswordResponse(
    val message: String,
    val codigo_dev: String? = null  // Solo visible en modo desarrollo
)

data class ResetPasswordRequest(
    val correo: String,
    val codigo: String,
    val nueva_password: String
)

// --- Comunidades / Publicaciones ---
data class PublicacionRequest(val contenido: String, val es_oficial: Boolean = false)
data class AutorResponse(val id: Int, val nombre: String, val email: String, val es_staff: Boolean = false)
data class PublicacionResponse(
    val id: Int,
    val comunidad_id: Int,
    val autor_id: Int,
    val contenido: String,
    val fecha_creacion: String,
    val likes_count: Int,
    val estado: String,
    val es_oficial: Boolean,
    val autor: AutorResponse
)

data class MessageResponse(val message: String)

interface ApiService {

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<TokenResponse>

    @POST("usuarios/")
    suspend fun crearUsuario(@Body usuario: UsuarioCreate): Response<UsuarioResponse>

    @GET("usuarios/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<UsuarioResponse>

    /** Actualiza campos del perfil del usuario autenticado (correo recuperación, huella, etc.) */
    @PATCH("usuarios/me")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body datos: UsuarioUpdateRequest
    ): Response<UsuarioResponse>

    /** Solicita el código de recuperación (lo envía al correo en prod, lo devuelve en dev) */
    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Query("correo") correo: String
    ): Response<ForgotPasswordResponse>

    /** Valida el código y actualiza la contraseña */
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>
    /** Obtiene publicaciones aprobadas de una comunidad */
    @GET("comunidades/{id}/publicaciones")
    suspend fun getPublicaciones(
        @Header("Authorization") token: String,
        @Path("id") comunidadId: Int,
        @Query("limit") limit: Int = 20
    ): Response<List<PublicacionResponse>>

    /** Publica un nuevo post en la comunidad */
    @POST("comunidades/{id}/publicaciones")
    suspend fun crearPublicacion(
        @Header("Authorization") token: String,
        @Path("id") comunidadId: Int,
        @Body publicacion: PublicacionRequest
    ): Response<PublicacionResponse>
}

package com.devcore.uat.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val access_token: String, val token_type: String)

data class UsuarioCreate(val nombre: String, val email: String, val password: String)
data class UsuarioResponse(
    val id: Int?,
    val nombre: String?,
    val email: String?,
    val nivel_confianza: Int?,
    val racha_diaria: Int?,
    val ultima_conexion: String?,
    val correo_recuperacion: String?,
    val fecha_nacimiento: String?,
    val semestre: Int?,
    val grupo: String?,
    val huella_habilitada: Boolean?,
    val es_staff: Boolean?
)

/** Payload para PATCH /usuarios/me — todos los campos son opcionales */
data class UsuarioUpdateRequest(
    val correo_recuperacion: String? = null,
    val fecha_nacimiento: String? = null,  // formato ISO: "YYYY-MM-DD"
    val semestre: Int? = null,
    val grupo: String? = null,
    val huella_habilitada: Boolean? = null
)

data class UsuarioResumen(
    val id: Int,
    val nombre: String,
    val email: String,
    val es_staff: Boolean?
)

data class PublicacionComunidad(
    val id: Int,
    val comunidad_id: Int,
    val autor_id: Int,
    val fecha_creacion: String,
    val likes_count: Int,
    val autor: UsuarioResumen,
    val contenido: String,
    val estado: String?,
    val es_oficial: Boolean?
)

data class PublicacionComunidadCreate(
    val contenido: String,
    val es_oficial: Boolean = false
)

data class PublicacionEstadoUpdate(
    val estado: String
)

interface ApiService {
    @POST("login")
    @retrofit2.http.FormUrlEncoded
    suspend fun login(
        @retrofit2.http.Field("username") username: String,
        @retrofit2.http.Field("password") password: String
    ): Response<TokenResponse>

    @POST("usuarios/")
    suspend fun crearUsuario(@Body usuario: UsuarioCreate): Response<UsuarioResponse>

    @GET("usuarios/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<UsuarioResponse>

    @PATCH("usuarios/me")
    suspend fun updateMe(
        @Header("Authorization") token: String,
        @Body datos: UsuarioUpdateRequest
    ): Response<UsuarioResponse>

    @GET("comunidades/{comunidad_id}/publicaciones")
    suspend fun obtenerPublicaciones(
        @retrofit2.http.Path("comunidad_id") comunidadId: Int,
        @retrofit2.http.Query("skip") skip: Int = 0,
        @retrofit2.http.Query("limit") limit: Int = 20,
        @Header("Authorization") token: String? = null
    ): Response<List<PublicacionComunidad>>

    @GET("comunidades/{comunidad_id}/pendientes")
    suspend fun obtenerPublicacionesPendientes(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("comunidad_id") comunidadId: Int
    ): Response<List<PublicacionComunidad>>

    @POST("comunidades/{comunidad_id}/publicaciones")
    suspend fun crearPublicacion(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("comunidad_id") comunidadId: Int,
        @Body publicacion: PublicacionComunidadCreate
    ): Response<PublicacionComunidad>

    @PATCH("comunidades/{comunidad_id}/publicaciones/{pub_id}/estado")
    suspend fun cambiarEstadoPublicacion(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("comunidad_id") comunidadId: Int,
        @retrofit2.http.Path("pub_id") pubId: Int,
        @Body actualizacion: PublicacionEstadoUpdate
    ): Response<PublicacionComunidad>

    @GET("usuarios/buscar")
    suspend fun buscarUsuarios(
        @retrofit2.http.Query("q") query: String
    ): Response<List<UsuarioResumen>>

    @GET("usuarios/{usuario_id}")
    suspend fun obtenerPerfilUsuario(
        @retrofit2.http.Path("usuario_id") usuarioId: Int
    ): Response<UsuarioResponse>

    @GET("usuarios/{usuario_id}/publicaciones")
    suspend fun obtenerPublicacionesUsuario(
        @retrofit2.http.Path("usuario_id") usuarioId: Int,
        @retrofit2.http.Query("skip") skip: Int = 0,
        @retrofit2.http.Query("limit") limit: Int = 20
    ): Response<List<PublicacionComunidad>>
}

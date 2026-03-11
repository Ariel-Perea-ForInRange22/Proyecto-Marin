package com.devcore.uat.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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
    val ultima_conexion: String?
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
}

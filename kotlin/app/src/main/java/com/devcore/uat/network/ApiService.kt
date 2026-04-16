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
    val telefono_recuperacion: String?,
    val fecha_nacimiento: String?,
    val semestre: Int?,
    val grupo: String?,
    val huella_habilitada: Boolean?,
    val es_staff: Boolean?
)

/** Payload para PATCH /usuarios/me â€” todos los campos son opcionales */
data class UsuarioUpdateRequest(
    val correo_recuperacion: String? = null,
    val telefono_recuperacion: String? = null,
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

data class ComentarioPublicacionResponse(
    val id: Int,
    val publicacion_id: Int,
    val autor_id: Int,
    val fecha_creacion: String,
    val contenido: String,
    val autor: UsuarioResumen
)

data class ComentarioPublicacionCreate(
    val contenido: String
)

data class PublicacionComunidad(
    val id: Int,
    val comunidad_id: Int,
    val autor_id: Int,
    val fecha_creacion: String,
    var likes_count: Int,
    val autor: UsuarioResumen,
    val contenido: String,
    val estado: String?,
    val es_oficial: Boolean?,
    val comunidad_nombre: String? = null,
    var usuario_ha_reaccionado: Boolean? = false,
    val comentarios: List<ComentarioPublicacionResponse> = emptyList()
)

data class PublicacionComunidadCreate(
    val contenido: String,
    val es_oficial: Boolean = false
)

data class PublicacionEstadoUpdate(
    val estado: String
)

// --- Recovery System ---

data class RecoveryCodesResponse(
    val codigos: List<String>,
    val mensaje: String,
    val total: Int
)

data class RecoveryCodesStatus(
    val total: Int,
    val disponibles: Int
)

data class RecoveryRequestEmail(
    val email: String
)

data class RecoveryVerifyEmail(
    val email: String,
    val codigo: String
)

data class RecoveryVerifyBackup(
    val email: String,
    val codigo: String
)

data class ResetPasswordRequest(
    val reset_token: String,
    val nueva_password: String
)

data class ResetTokenResponse(
    val reset_token: String,
    val mensaje: String
)

data class ResetPasswordResponse(
    val mensaje: String
)

// --- Bus Tracking Data Classes ---

data class UbicacionCreate(
    val latitud: Double,
    val longitud: Double
)

data class PuntoCalor(
    val latitud: Double,
    val longitud: Double,
    val cantidad: Int
)

data class ReporteBusCreate(
    val tipo: String,
    val zona: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)

data class ReporteBusResponse(
    val id: Int,
    val tipo: String,
    val zona: String?,
    val latitud: Double?,
    val longitud: Double?,
    val confirmaciones: Int,
    val timestamp: String,
    val autor_nombre: String
)

data class ConfirmacionResponse(
    val confirmaciones: Int
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

    @GET("publicaciones/feed")
    suspend fun obtenerFeedGlobal(
        @retrofit2.http.Query("skip") skip: Int = 0,
        @retrofit2.http.Query("limit") limit: Int = 30,
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

    // --- Recovery Endpoints ---

    @POST("recovery/generate-codes")
    suspend fun generarCodigosRespaldo(
        @Header("Authorization") token: String
    ): Response<RecoveryCodesResponse>

    @GET("recovery/codes-status")
    suspend fun estadoCodigosRespaldo(
        @Header("Authorization") token: String
    ): Response<RecoveryCodesStatus>

    @POST("recovery/request-email")
    suspend fun solicitarCodigoPorEmail(
        @Body datos: RecoveryRequestEmail
    ): Response<ResetTokenResponse>

    @POST("recovery/verify-email-code")
    suspend fun verificarCodigoEmail(
        @Body datos: RecoveryVerifyEmail
    ): Response<ResetTokenResponse>

    @POST("recovery/verify-backup-code")
    suspend fun verificarCodigoRespaldo(
        @Body datos: RecoveryVerifyBackup
    ): Response<ResetTokenResponse>

    @POST("recovery/reset-password")
    suspend fun restablecerPassword(
        @Body datos: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    // --- Marketplace Endpoints ---

    @GET("productos/")
    suspend fun listarProductos(
        @retrofit2.http.Query("skip") skip: Int = 0,
        @retrofit2.http.Query("limit") limit: Int = 20,
        @retrofit2.http.Query("categoria") categoria: String? = null,
        @retrofit2.http.Query("q") query: String? = null
    ): Response<List<ProductoResponse>>

    @GET("productos/mis-productos")
    suspend fun misProductos(
        @Header("Authorization") token: String
    ): Response<List<ProductoResponse>>

    @POST("productos/")
    suspend fun crearProducto(
        @Header("Authorization") token: String,
        @Body producto: ProductoCreateRequest
    ): Response<ProductoResponse>

    @PATCH("productos/{producto_id}")
    suspend fun actualizarProducto(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("producto_id") productoId: Int,
        @Body datos: ProductoUpdateRequest
    ): Response<ProductoResponse>

    @retrofit2.http.DELETE("productos/{producto_id}")
    suspend fun eliminarProducto(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("producto_id") productoId: Int
    ): Response<DeleteResponse>

    @retrofit2.http.Multipart
    @POST("productos/{producto_id}/imagen")
    suspend fun uploadImagenProducto(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("producto_id") productoId: Int,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part
    ): Response<ProductoResponse>

    // --- Bus Tracking Endpoints ---

    @POST("bus/ubicacion")
    suspend fun reportarUbicacionActiva(
        @Header("Authorization") token: String,
        @Body ubicacion: UbicacionCreate
    ): Response<Void> // Response type is UbicacionResponse but we don't strictly need it on android right now

    @retrofit2.http.DELETE("bus/ubicacion")
    suspend fun borrarUbicacionActiva(
        @Header("Authorization") token: String
    ): Response<Void>

    @GET("bus/puntos-calor")
    suspend fun obtenerPuntosCalor(): Response<List<PuntoCalor>>

    @POST("bus/reportes")
    suspend fun crearReporteBus(
        @Header("Authorization") token: String,
        @Body reporte: ReporteBusCreate
    ): Response<ReporteBusResponse>

    @GET("bus/reportes")
    suspend fun obtenerReportesBus(): Response<List<ReporteBusResponse>>

    @POST("bus/reportes/{reporte_id}/confirmar")
    suspend fun confirmarReporteBus(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("reporte_id") reporteId: Int
    ): Response<ConfirmacionResponse>

    // --- Reacciones y Comentarios ---

    @POST("publicaciones/{pub_id}/reaccionar")
    suspend fun reaccionarPublicacion(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("pub_id") pubId: Int
    ): Response<ReaccionResponse>

    @POST("publicaciones/{pub_id}/comentarios")
    suspend fun comentarPublicacion(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("pub_id") pubId: Int,
        @Body comentario: ComentarioPublicacionCreate
    ): Response<ComentarioPublicacionResponse>

    @retrofit2.http.DELETE("publicaciones/{pub_id}")
    suspend fun borrarPublicacion(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("pub_id") pubId: Int
    ): Response<DeleteResponse>

    @retrofit2.http.DELETE("publicaciones/comentarios/{com_id}")
    suspend fun borrarComentario(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("com_id") comId: Int
    ): Response<DeleteResponse>

    @GET("comunidades/{comunidad_id}/miembros")
    suspend fun buscarMiembrosComunidad(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("comunidad_id") comunidadId: Int,
        @retrofit2.http.Query("q") q: String = ""
    ): Response<List<UsuarioResumen>>
}

// --- Marketplace Data Classes ---

data class ProductoVendedor(
    val id: Int,
    val nombre: String,
    val email: String
)

data class ProductoResponse(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val precio: Double,
    val categoria: String,
    val condicion: String,
    val estado: String,
    val fecha_creacion: String?,
    val imagen_url: String?,
    val vendedor_id: Int,
    val vendedor: ProductoVendedor?
)

data class ProductoCreateRequest(
    val titulo: String,
    val descripcion: String?,
    val precio: Double,
    val categoria: String,
    val condicion: String = "usado"
)

data class ProductoUpdateRequest(
    val titulo: String? = null,
    val descripcion: String? = null,
    val precio: Double? = null,
    val categoria: String? = null,
    val condicion: String? = null,
    val estado: String? = null
)

data class DeleteResponse(
    val detail: String
)

data class ReaccionResponse(
    val liked: Boolean,
    val likes_count: Int
)


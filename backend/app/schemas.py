from pydantic import BaseModel, EmailStr
from typing import Optional, List
from datetime import datetime, date

# --- AUTH (TOKENS) ---
class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: Optional[str] = None

# --- USUARIOS ---
class UsuarioBase(BaseModel):
    nombre: str
    email: EmailStr

class UsuarioCreate(UsuarioBase):
    password: str

class Usuario(UsuarioBase):
    id: int
    es_staff: bool = False
    nivel_confianza: int
    racha_diaria: int
    ultima_conexion: datetime
    correo_recuperacion: Optional[str] = None
    telefono_recuperacion: Optional[str] = None
    fecha_nacimiento: Optional[date] = None
    semestre: Optional[int] = None
    grupo: Optional[str] = None
    huella_habilitada: bool = False

    class Config:
        from_attributes = True  # Pydantic V2 (antes orm_mode=True)

# --- ACTUALIZACIÓN DE PERFIL (PATCH) ---
class UsuarioUpdate(BaseModel):
    correo_recuperacion: Optional[EmailStr] = None
    telefono_recuperacion: Optional[str] = None
    fecha_nacimiento: Optional[date] = None
    semestre: Optional[int] = None
    grupo: Optional[str] = None
    huella_habilitada: Optional[bool] = None

# --- PRODUCTOS ---
class ProductoBase(BaseModel):
    titulo: str
    descripcion: Optional[str] = None
    precio: float
    categoria: str  # libros, electronicos, ropa, accesorios, otros
    condicion: str = "usado"  # nuevo, como_nuevo, usado

class ProductoCreate(ProductoBase):
    pass

class ProductoUpdate(BaseModel):
    titulo: Optional[str] = None
    descripcion: Optional[str] = None
    precio: Optional[float] = None
    categoria: Optional[str] = None
    condicion: Optional[str] = None
    estado: Optional[str] = None  # activo, vendido, pausado

class ProductoVendedor(BaseModel):
    id: int
    nombre: str
    email: str
    class Config:
        from_attributes = True

class Producto(ProductoBase):
    id: int
    vendedor_id: int
    estado: str = "activo"
    imagen_url: Optional[str] = None
    fecha_creacion: Optional[datetime] = None
    vendedor: Optional[ProductoVendedor] = None

    class Config:
        from_attributes = True

# TODO: Integrar schemas de reportes y ubicaciones cuando los modelos esten listos

# --- COMUNIDADES ---
class ComunidadBase(BaseModel):
    nombre: str
    descripcion: Optional[str] = None

class ComunidadCreate(ComunidadBase):
    pass

class UsuarioResumen(BaseModel):
    """Versión reducida del usuario para listas de miembros"""
    id: int
    nombre: str
    email: str
    es_staff: bool = False

    class Config:
        from_attributes = True

class Comunidad(ComunidadBase):
    id: int
    fecha_creacion: datetime
    total_miembros: int = 0
    soy_miembro: bool = False

    class Config:
        from_attributes = True

# --- PUBLICACIONES COMUNIDAD ---
class PublicacionComunidadBase(BaseModel):
    contenido: str
    es_oficial: bool = False

class PublicacionComunidadCreate(PublicacionComunidadBase):
    pass

class PublicacionEstadoUpdate(BaseModel):
    estado: str # PENDIENTE, APROBADA, RECHAZADA

class PublicacionComunidad(PublicacionComunidadBase):
    id: int
    comunidad_id: int
    autor_id: int
    fecha_creacion: datetime
    likes_count: int
    estado: str
    autor: UsuarioResumen

    class Config:
        from_attributes = True

# --- RECUPERACIÓN DE CUENTA ---

class RecoveryCodesResponse(BaseModel):
    """Respuesta al generar códigos de respaldo (se muestran una sola vez)"""
    codigos: List[str]
    mensaje: str
    total: int

class RecoveryCodesStatus(BaseModel):
    """Cuántos códigos quedan disponibles"""
    total: int
    disponibles: int

class RecoveryRequestEmail(BaseModel):
    """Solicitar código de recuperación por correo secundario"""
    email: EmailStr  # correo institucional del usuario

class RecoveryVerifyEmail(BaseModel):
    """Verificar código de 6 dígitos enviado al correo secundario"""
    email: EmailStr
    codigo: str

class RecoveryVerifyBackup(BaseModel):
    """Verificar código de respaldo"""
    email: EmailStr
    codigo: str

class ResetPasswordRequest(BaseModel):
    """Restablecer contraseña con token temporal"""
    reset_token: str
    nueva_password: str

class ResetTokenResponse(BaseModel):
    """Token temporal para restablecer contraseña"""
    reset_token: str
    mensaje: str


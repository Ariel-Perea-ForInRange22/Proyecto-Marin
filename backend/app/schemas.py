from pydantic import BaseModel, EmailStr
from typing import Optional
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
    fecha_nacimiento: Optional[date] = None
    semestre: Optional[int] = None
    grupo: Optional[str] = None
    huella_habilitada: bool = False

    class Config:
        from_attributes = True

# --- ACTUALIZACIÓN DE PERFIL (PATCH) ---
class UsuarioUpdate(BaseModel):
    correo_recuperacion: Optional[EmailStr] = None
    fecha_nacimiento: Optional[date] = None
    semestre: Optional[int] = None
    grupo: Optional[str] = None
    huella_habilitada: Optional[bool] = None

# --- RECUPERACIÓN DE CONTRASEÑA ---
class ResetPasswordRequest(BaseModel):
    correo: str
    codigo: str
    nueva_password: str

# --- PRODUCTOS ---
CATEGORIAS_VALIDAS = {"libros", "electronicos", "ropa", "accesorios", "comida", "otros"}

class ProductoBase(BaseModel):
    titulo: str
    descripcion: Optional[str] = None
    precio: float
    categoria: str = "otros"
    imagen_url: Optional[str] = None

class ProductoCreate(ProductoBase):
    pass

class ProductoVendedor(BaseModel):
    id: int
    nombre: str
    email: str

    class Config:
        from_attributes = True

class Producto(ProductoBase):
    id: int
    vendedor_id: int
    es_patrocinado: bool = False
    fecha_publicacion: Optional[datetime] = None
    vendedor: Optional[ProductoVendedor] = None

    class Config:
        from_attributes = True

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
    estado: str  # PENDIENTE, APROBADA, RECHAZADA

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

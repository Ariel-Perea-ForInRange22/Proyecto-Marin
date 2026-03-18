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
        from_attributes = True  # Pydantic V2 (antes orm_mode=True)

# --- ACTUALIZACIÓN DE PERFIL (PATCH) ---
class UsuarioUpdate(BaseModel):
    correo_recuperacion: Optional[EmailStr] = None
    fecha_nacimiento: Optional[date] = None
    semestre: Optional[int] = None
    grupo: Optional[str] = None
    huella_habilitada: Optional[bool] = None

# --- PRODUCTOS ---
class ProductoBase(BaseModel):
    titulo: str
    descripcion: Optional[str] = None
    precio: float

class ProductoCreate(ProductoBase):
    pass

class Producto(ProductoBase):
    id: int
    vendedor_id: int

    class Config:
        from_attributes = True

# TODO: Integrar schemas de reportes y ubicaciones cuando los modelos estén listos

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

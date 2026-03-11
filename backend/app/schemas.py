from pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime

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
    nivel_confianza: int
    racha_diaria: int
    ultima_conexion: datetime

    class Config:
        from_attributes = True  # Pydantic V2 (antes orm_mode=True)

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

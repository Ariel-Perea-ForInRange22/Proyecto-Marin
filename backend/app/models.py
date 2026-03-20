from sqlalchemy import Boolean, Column, Date, ForeignKey, Integer, String, Float, DateTime
from sqlalchemy.orm import relationship
import datetime
from .database import Base

class Usuario(Base):
    __tablename__ = "usuarios"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, index=True)
    email = Column(String, unique=True, index=True)
    hashed_password = Column(String)
    
    es_staff = Column(Boolean, default=False)
    nivel_confianza = Column(Integer, default=0)
    racha_diaria = Column(Integer, default=0)
    ultima_conexion = Column(DateTime, default=datetime.datetime.utcnow)

    # Campos de configuración de perfil extendida
    correo_recuperacion = Column(String, nullable=True)
    fecha_nacimiento = Column(Date, nullable=True)
    semestre = Column(Integer, nullable=True)  # 1 a 9
    grupo = Column(String, nullable=True)       # Ej: 'A', 'B', 'C'
    huella_habilitada = Column(Boolean, default=False)

    # Campos para el flujo de recuperación de contraseña
    reset_token = Column(String, nullable=True)
    reset_token_expiry = Column(DateTime, nullable=True)

    # Relaciones
    comunidades = relationship("Comunidad", secondary="usuario_comunidad", back_populates="miembros")
    cursos = relationship("Curso", secondary="usuario_curso", back_populates="estudiantes")
    publicaciones_comunidad = relationship("PublicacionComunidad", back_populates="autor")

class Producto(Base):
    __tablename__ = "productos"

    id = Column(Integer, primary_key=True, index=True)
    titulo = Column(String, index=True)
    descripcion = Column(String, index=True)
    precio = Column(Float)
    categoria = Column(String, default="otros")  # libros, electronicos, ropa, accesorios, comida, otros
    imagen_url = Column(String, nullable=True)   # URL o emoji para muestra
    es_patrocinado = Column(Boolean, default=False)  # True = promotor, False = alumno vendedor
    fecha_publicacion = Column(DateTime, default=datetime.datetime.utcnow)
    vendedor_id = Column(Integer, ForeignKey("usuarios.id"))

    vendedor = relationship("Usuario", foreign_keys=[vendedor_id])

class UsuarioComunidad(Base):
    __tablename__ = "usuario_comunidad"
    usuario_id = Column(Integer, ForeignKey("usuarios.id"), primary_key=True)
    comunidad_id = Column(Integer, ForeignKey("comunidades.id"), primary_key=True)
    rol = Column(String, default="MIEMBRO")
    fecha_union = Column(DateTime, default=datetime.datetime.utcnow)

class UsuarioCurso(Base):
    __tablename__ = "usuario_curso"
    usuario_id = Column(Integer, ForeignKey("usuarios.id"), primary_key=True)
    curso_id = Column(Integer, ForeignKey("cursos.id"), primary_key=True)
    fecha_inscripcion = Column(DateTime, default=datetime.datetime.utcnow)

class Comunidad(Base):
    __tablename__ = "comunidades"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, index=True, unique=True)
    descripcion = Column(String)
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)
    
    miembros = relationship("Usuario", secondary="usuario_comunidad", back_populates="comunidades")
    publicaciones = relationship("PublicacionComunidad", back_populates="comunidad")

class PublicacionComunidad(Base):
    __tablename__ = "publicaciones_comunidad"

    id = Column(Integer, primary_key=True, index=True)
    comunidad_id = Column(Integer, ForeignKey("comunidades.id"))
    autor_id = Column(Integer, ForeignKey("usuarios.id"))
    contenido = Column(String)
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)
    likes_count = Column(Integer, default=0)
    estado = Column(String, default="PENDIENTE")
    es_oficial = Column(Boolean, default=False)

    comunidad = relationship("Comunidad", back_populates="publicaciones")
    autor = relationship("Usuario", back_populates="publicaciones_comunidad")

class Curso(Base):
    __tablename__ = "cursos"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, index=True)
    codigo = Column(String, unique=True, index=True)
    creditos = Column(Integer)
    
    estudiantes = relationship("Usuario", secondary="usuario_curso", back_populates="cursos")

from sqlalchemy import Boolean, Column, Date, ForeignKey, Integer, String, Float, DateTime
from sqlalchemy.orm import relationship
import datetime
from .database import Base

class Usuario(Base):
    __tablename__ = "usuarios"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, index=True)
    email = Column(String, unique=True, index=True)
    
    # TODO: Implementar un hash real para contraseñas antes del release final
    hashed_password = Column(String)
    
    es_staff = Column(Boolean, default=False)
    nivel_confianza = Column(Integer, default=0)
    racha_diaria = Column(Integer, default=0)
    ultima_conexion = Column(DateTime, default=datetime.datetime.utcnow)

    # Campos de configuración de perfil extendida
    correo_recuperacion = Column(String, nullable=True)
    telefono_recuperacion = Column(String, nullable=True)
    fecha_nacimiento = Column(Date, nullable=True)
    semestre = Column(Integer, nullable=True)  # 1 a 9
    grupo = Column(String, nullable=True)       # Ej: 'A', 'B', 'C'
    huella_habilitada = Column(Boolean, default=False)

    # Relaciones (Comentadas por ahora para ir construyendo por partes)
    # productos = relationship("Producto", back_populates="vendedor")
    # reportes = relationship("Reporte", back_populates="autor")
    # ubicaciones = relationship("UbicacionBus", back_populates="usuario")
    comunidades = relationship("Comunidad", secondary="usuario_comunidad", back_populates="miembros")
    cursos = relationship("Curso", secondary="usuario_curso", back_populates="estudiantes")
    publicaciones_comunidad = relationship("PublicacionComunidad", back_populates="autor")
    codigos_recuperacion = relationship("CodigoRecuperacion", back_populates="usuario", cascade="all, delete-orphan")

class CodigoRecuperacion(Base):
    """Códigos de respaldo (backup codes) estilo Google/Facebook.
    Se generan 8, se guardan hasheados, cada uno es de un solo uso."""
    __tablename__ = "codigos_recuperacion"

    id = Column(Integer, primary_key=True, index=True)
    usuario_id = Column(Integer, ForeignKey("usuarios.id"), nullable=False)
    codigo_hash = Column(String, nullable=False)
    usado = Column(Boolean, default=False)
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)

    usuario = relationship("Usuario", back_populates="codigos_recuperacion")

class Producto(Base):
    __tablename__ = "productos"

    id = Column(Integer, primary_key=True, index=True)
    titulo = Column(String, index=True)
    descripcion = Column(String)
    precio = Column(Float)
    categoria = Column(String, index=True)  # libros, electronicos, ropa, accesorios, otros
    condicion = Column(String, default="usado")  # nuevo, como_nuevo, usado
    estado = Column(String, default="activo")  # activo, vendido, pausado
    imagen_url = Column(String, nullable=True)
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)
    
    # FK a usuario vendedor
    vendedor_id = Column(Integer, ForeignKey("usuarios.id"))
    vendedor = relationship("Usuario", backref="productos")

# Tabla intermedia para la relación de muchos-a-muchos entre Usuarios y Comunidades
class UsuarioComunidad(Base):
    __tablename__ = "usuario_comunidad"
    usuario_id = Column(Integer, ForeignKey("usuarios.id"), primary_key=True)
    comunidad_id = Column(Integer, ForeignKey("comunidades.id"), primary_key=True)
    rol = Column(String, default="MIEMBRO") # Puede ser "ADMIN" o "MIEMBRO"
    fecha_union = Column(DateTime, default=datetime.datetime.utcnow)

# Tabla intermedia para la relación de muchos-a-muchos entre Usuarios y Cursos
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
    
    # Usuarios miembros de esta comunidad
    miembros = relationship("Usuario", secondary="usuario_comunidad", back_populates="comunidades")
    
    # Publicaciones en el muro de esta comunidad
    publicaciones = relationship("PublicacionComunidad", back_populates="comunidad")

class PublicacionComunidad(Base):
    __tablename__ = "publicaciones_comunidad"

    id = Column(Integer, primary_key=True, index=True)
    comunidad_id = Column(Integer, ForeignKey("comunidades.id"))
    autor_id = Column(Integer, ForeignKey("usuarios.id"))
    contenido = Column(String)
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)
    likes_count = Column(Integer, default=0)
    estado = Column(String, default="PENDIENTE") # PENDIENTE, APROBADA, RECHAZADA
    es_oficial = Column(Boolean, default=False)

    # Relaciones
    comunidad = relationship("Comunidad", back_populates="publicaciones")
    autor = relationship("Usuario", back_populates="publicaciones_comunidad")
    comentarios = relationship("ComentarioPublicacion", back_populates="publicacion", cascade="all, delete-orphan")
    reacciones = relationship("ReaccionPublicacion", back_populates="publicacion", cascade="all, delete-orphan")

class ComentarioPublicacion(Base):
    __tablename__ = "comentarios_publicacion"
    id = Column(Integer, primary_key=True, index=True)
    publicacion_id = Column(Integer, ForeignKey("publicaciones_comunidad.id"))
    autor_id = Column(Integer, ForeignKey("usuarios.id"))
    contenido = Column(String)
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)

    publicacion = relationship("PublicacionComunidad", back_populates="comentarios")
    autor = relationship("Usuario", backref="comentarios_publicacion")

class ReaccionPublicacion(Base):
    __tablename__ = "reacciones_publicacion"
    id = Column(Integer, primary_key=True, index=True)
    publicacion_id = Column(Integer, ForeignKey("publicaciones_comunidad.id"))
    usuario_id = Column(Integer, ForeignKey("usuarios.id"))
    tipo = Column(String, default="LIKE") 
    fecha_creacion = Column(DateTime, default=datetime.datetime.utcnow)

    publicacion = relationship("PublicacionComunidad", back_populates="reacciones")
    usuario = relationship("Usuario", backref="reacciones_publicacion")


class Curso(Base):
    __tablename__ = "cursos"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, index=True)
    codigo = Column(String, unique=True, index=True) # Ejemplo: MAT-101
    creditos = Column(Integer)
    
    # Estudiantes inscritos en este curso
    estudiantes = relationship("Usuario", secondary="usuario_curso", back_populates="cursos")

# Se agregan al final las relaciones inversas para la clase Usuario (se deben agregar a Usuario también, 
# pero SQLAlchemy permite inyectarlas dinámicamente o definirlas allí. Las pondremos en Usuario).

# ─────────────────────────────────────────
#  BUS TRACKING - Modelos
# ─────────────────────────────────────────

class UbicacionAlumno(Base):
    """Ubicación GPS activa de un alumno (expira en 10 minutos)."""
    __tablename__ = "ubicaciones_alumnos"

    id          = Column(Integer, primary_key=True, index=True)
    usuario_id  = Column(Integer, ForeignKey("usuarios.id"), nullable=False)
    latitud     = Column(Float, nullable=False)
    longitud    = Column(Float, nullable=False)
    timestamp   = Column(DateTime, default=datetime.datetime.utcnow)
    activa      = Column(Boolean, default=True)

    usuario = relationship("Usuario", backref="ubicaciones_bus")


class ReporteBus(Base):
    """Reporte manual de un alumno sobre el estado del autobús."""
    __tablename__ = "reportes_bus"

    id              = Column(Integer, primary_key=True, index=True)
    usuario_id      = Column(Integer, ForeignKey("usuarios.id"), nullable=False)
    tipo            = Column(String, nullable=False)   # "ya_paso" | "no_paso"
    zona            = Column(String, nullable=True)    # Descripción libre, ej. "Frente a FIME"
    latitud         = Column(Float, nullable=True)
    longitud        = Column(Float, nullable=True)
    confirmaciones  = Column(Integer, default=0)
    timestamp       = Column(DateTime, default=datetime.datetime.utcnow)

    usuario = relationship("Usuario", backref="reportes_bus")

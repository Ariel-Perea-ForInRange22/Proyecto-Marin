from sqlalchemy import Boolean, Column, ForeignKey, Integer, String, Float, DateTime
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
    
    nivel_confianza = Column(Integer, default=0)
    racha_diaria = Column(Integer, default=0)
    ultima_conexion = Column(DateTime, default=datetime.datetime.utcnow)

    # Relaciones (Comentadas por ahora para ir construyendo por partes)
    # productos = relationship("Producto", back_populates="vendedor")
    # reportes = relationship("Reporte", back_populates="autor")
    # ubicaciones = relationship("UbicacionBus", back_populates="usuario")
    comunidades = relationship("Comunidad", secondary="usuario_comunidad", back_populates="miembros")
    cursos = relationship("Curso", secondary="usuario_curso", back_populates="estudiantes")

class Producto(Base):
    __tablename__ = "productos"

    id = Column(Integer, primary_key=True, index=True)
    titulo = Column(String, index=True)
    descripcion = Column(String, index=True)
    precio = Column(Float)
    
    # FK a usuario vendedor
    vendedor_id = Column(Integer, ForeignKey("usuarios.id"))

# Tabla intermedia para la relación de muchos-a-muchos entre Usuarios y Comunidades
class UsuarioComunidad(Base):
    __tablename__ = "usuario_comunidad"
    usuario_id = Column(Integer, ForeignKey("usuarios.id"), primary_key=True)
    comunidad_id = Column(Integer, ForeignKey("comunidades.id"), primary_key=True)
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

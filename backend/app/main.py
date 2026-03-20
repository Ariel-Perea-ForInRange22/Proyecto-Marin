from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import timedelta, datetime
from jose import JWTError, jwt
import random
import string

from . import models, schemas
from .database import engine, get_db
from .auth import verify_password, get_password_hash, create_access_token, ALGORITHM, SECRET_KEY, ACCESS_TOKEN_EXPIRE_MINUTES

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        email: str = payload.get("sub")
        if email is None:
            raise credentials_exception
        token_data = schemas.TokenData(email=email)
    except JWTError:
        raise credentials_exception
    user = db.query(models.Usuario).filter(models.Usuario.email == token_data.email).first()
    if user is None:
        raise credentials_exception
    return user


# Crea las tablas en la Base de Datos al arrancar la app
models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Comunidad Universitaria API (Preview)",
    description="Backend para la app móvil de la universidad - **FASE DE DESARROLLO**",
    version="0.1.0"
)

# CORS para permitir el mockup web y futuro frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def read_root():
    return {"status": "En desarrollo", "message": "API de la Comunidad Universitaria funcionando."}

# --- ENDPOINTS BÁSICOS: USUARIOS ---

@app.post("/usuarios/", response_model=schemas.Usuario)
def crear_usuario(usuario: schemas.UsuarioCreate, db: Session = Depends(get_db)):
    db_user = db.query(models.Usuario).filter(models.Usuario.email == usuario.email).first()
    if db_user:
        raise HTTPException(status_code=400, detail="El email ya está registrado")
    
    hashed_password = get_password_hash(usuario.password)
    
    nuevo_usuario = models.Usuario(
        nombre=usuario.nombre,
        email=usuario.email,
        hashed_password=hashed_password,
        nivel_confianza=0,
        racha_diaria=0,
        ultima_conexion=datetime.utcnow()
    )
    db.add(nuevo_usuario)
    db.commit()
    db.refresh(nuevo_usuario)
    
    return nuevo_usuario

@app.post("/login", response_model=schemas.Token)
def login_for_access_token(db: Session = Depends(get_db), form_data: OAuth2PasswordRequestForm = Depends()):
    user = db.query(models.Usuario).filter(models.Usuario.email == form_data.username).first()
    if not user or not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@app.get("/usuarios/me", response_model=schemas.Usuario)
def read_users_me(current_user: models.Usuario = Depends(get_current_user)):
    return current_user

@app.get("/usuarios/", response_model=List[schemas.Usuario])
def leer_usuarios(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    usuarios = db.query(models.Usuario).offset(skip).limit(limit).all()
    return usuarios

@app.patch("/usuarios/me", response_model=schemas.Usuario)
def actualizar_perfil(
    datos: schemas.UsuarioUpdate,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Actualiza los datos de configuración del perfil del usuario autenticado (PATCH parcial)."""
    update_data = datos.model_dump(exclude_unset=True)

    if "semestre" in update_data and update_data["semestre"] is not None:
        if not (1 <= update_data["semestre"] <= 9):
            raise HTTPException(status_code=400, detail="El semestre debe estar entre 1 y 9")

    for field, value in update_data.items():
        setattr(current_user, field, value)

    db.commit()
    db.refresh(current_user)
    return current_user


# --- ENDPOINTS: RECUPERACIÓN DE CONTRASEÑA ---

@app.post("/auth/forgot-password")
def forgot_password(correo: str, db: Session = Depends(get_db)):
    """
    Genera un código de 6 dígitos y lo guarda en el perfil del usuario.
    En producción deberías enviar el código por correo (smtplib / fastapi-mail).
    Por ahora lo retornamos en la respuesta para pruebas en desarrollo.
    """
    # Buscar por email principal O por correo de recuperación
    user = db.query(models.Usuario).filter(
        (models.Usuario.email == correo) |
        (models.Usuario.correo_recuperacion == correo)
    ).first()

    if not user:
        # Siempre respondemos con éxito para no revelar si el correo existe
        return {"message": "Si el correo existe, recibirás un código de recuperación."}

    # Generar código de 6 dígitos
    codigo = ''.join(random.choices(string.digits, k=6))
    expiracion = datetime.utcnow() + timedelta(minutes=15)

    user.reset_token = codigo
    user.reset_token_expiry = expiracion
    db.commit()

    # TODO: En producción, enviar el código al correo del usuario con smtplib o fastapi-mail
    # Por ahora lo devolvemos en la respuesta para desarrollo y pruebas locales
    return {
        "message": "Si el correo existe, recibirás un código de recuperación.",
        "codigo_dev": codigo  # ⚠️ SOLO PARA DESARROLLO - Remover en producción
    }


@app.post("/auth/reset-password")
def reset_password(datos: schemas.ResetPasswordRequest, db: Session = Depends(get_db)):
    """
    Valida el código de recuperación y actualiza la contraseña del usuario.
    """
    user = db.query(models.Usuario).filter(
        (models.Usuario.email == datos.correo) |
        (models.Usuario.correo_recuperacion == datos.correo)
    ).first()

    if not user:
        raise HTTPException(status_code=400, detail="Código inválido o expirado")

    if user.reset_token != datos.codigo:
        raise HTTPException(status_code=400, detail="Código inválido o expirado")

    if user.reset_token_expiry is None or datetime.utcnow() > user.reset_token_expiry:
        raise HTTPException(status_code=400, detail="El código ha expirado. Solicita uno nuevo.")

    # Actualizar contraseña
    user.hashed_password = get_password_hash(datos.nueva_password)
    user.reset_token = None
    user.reset_token_expiry = None
    db.commit()

    return {"message": "Contraseña actualizada exitosamente"}


# --- ENDPOINTS: BÚSQUEDA Y PERFILES ---

@app.get("/usuarios/buscar", response_model=List[schemas.UsuarioResumen])
def buscar_usuarios(q: str, db: Session = Depends(get_db)):
    if not q or len(q) < 2:
        return []
    
    termino = f"%{q}%"
    usuarios = db.query(models.Usuario)\
        .filter(models.Usuario.nombre.ilike(termino) | models.Usuario.email.ilike(termino))\
        .limit(20).all()
    return usuarios

@app.get("/usuarios/{usuario_id}", response_model=schemas.Usuario)
def obtener_perfil_usuario(usuario_id: int, db: Session = Depends(get_db)):
    user = db.query(models.Usuario).filter(models.Usuario.id == usuario_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    return user

@app.get("/usuarios/{usuario_id}/publicaciones", response_model=List[schemas.PublicacionComunidad])
def obtener_publicaciones_usuario(
    usuario_id: int, 
    skip: int = 0, 
    limit: int = 20, 
    db: Session = Depends(get_db)
):
    publicaciones = db.query(models.PublicacionComunidad)\
        .filter(models.PublicacionComunidad.autor_id == usuario_id)\
        .filter(models.PublicacionComunidad.estado == "APROBADA")\
        .order_by(models.PublicacionComunidad.fecha_creacion.desc())\
        .offset(skip).limit(limit).all()
    return publicaciones

# --- ENDPOINTS: COMUNIDADES Y MURAL ---

@app.get("/comunidades/{comunidad_id}/publicaciones", response_model=List[schemas.PublicacionComunidad])
def obtener_publicaciones_comunidad(
    comunidad_id: int, 
    skip: int = 0, 
    limit: int = 20, 
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    publicaciones = db.query(models.PublicacionComunidad)\
        .filter(models.PublicacionComunidad.comunidad_id == comunidad_id)\
        .filter(models.PublicacionComunidad.estado == "APROBADA")\
        .order_by(models.PublicacionComunidad.fecha_creacion.desc())\
        .offset(skip).limit(limit).all()
    return publicaciones

@app.get("/comunidades/{comunidad_id}/pendientes", response_model=List[schemas.PublicacionComunidad])
def obtener_publicaciones_pendientes(
    comunidad_id: int, 
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if not current_user.es_staff:
        raise HTTPException(status_code=403, detail="No tienes permisos para moderar")

    publicaciones = db.query(models.PublicacionComunidad)\
        .filter(models.PublicacionComunidad.comunidad_id == comunidad_id)\
        .filter(models.PublicacionComunidad.estado == "PENDIENTE")\
        .order_by(models.PublicacionComunidad.fecha_creacion.asc())\
        .all()
    return publicaciones

@app.post("/comunidades/{comunidad_id}/publicaciones", response_model=schemas.PublicacionComunidad)
def crear_publicacion(
    comunidad_id: int, 
    publicacion: schemas.PublicacionComunidadCreate, 
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    comunidad = db.query(models.Comunidad).filter(models.Comunidad.id == comunidad_id).first()
    if not comunidad:
        raise HTTPException(status_code=404, detail="Comunidad no encontrada")
        
    estado_inicial = "PENDIENTE"
    if current_user.es_staff:
        estado_inicial = "APROBADA"
        
    nueva_pub = models.PublicacionComunidad(
        comunidad_id=comunidad_id,
        autor_id=current_user.id,
        contenido=publicacion.contenido,
        estado=estado_inicial,
        es_oficial=(publicacion.es_oficial if current_user.es_staff else False)
    )
    db.add(nueva_pub)
    db.commit()
    db.refresh(nueva_pub)
    return nueva_pub

@app.patch("/comunidades/{comunidad_id}/publicaciones/{pub_id}/estado", response_model=schemas.PublicacionComunidad)
def cambiar_estado_publicacion(
    comunidad_id: int,
    pub_id: int,
    actualizacion: schemas.PublicacionEstadoUpdate,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if not current_user.es_staff:
        raise HTTPException(status_code=403, detail="No tienes permisos para moderar")
        
    pub = db.query(models.PublicacionComunidad).filter(models.PublicacionComunidad.id == pub_id).first()
    if not pub or pub.comunidad_id != comunidad_id:
        raise HTTPException(status_code=404, detail="Publicación no encontrada")
        
    pub.estado = actualizacion.estado.upper()
    db.commit()
    db.refresh(pub)
    return pub

# TODO: Endpoints de Rastreo.
# Quedarán pendientes para la próxima semana.

# --- ENDPOINTS: PRODUCTOS (MERCADITO) ---

@app.get("/productos/", response_model=List[schemas.Producto])
def listar_productos(
    categoria: Optional[str] = None,
    tipo: Optional[str] = None,   # "patrocinador" | "alumno" | None (todos)
    skip: int = 0,
    limit: int = 40,
    db: Session = Depends(get_db)
):
    """
    Lista todos los productos del mercadito.
    - **categoria**: filtra por categoría (libros, electronicos, ropa, accesorios, comida, otros)
    - **tipo**: filtra por tipo de vendedor: 'patrocinador' o 'alumno'
    """
    query = db.query(models.Producto)

    if categoria:
        query = query.filter(models.Producto.categoria == categoria.lower())

    if tipo == "patrocinador":
        query = query.filter(models.Producto.es_patrocinado == True)
    elif tipo == "alumno":
        query = query.filter(models.Producto.es_patrocinado == False)

    productos = query.order_by(
        models.Producto.es_patrocinado.desc(),         # los patrocinados primero
        models.Producto.fecha_publicacion.desc()
    ).offset(skip).limit(limit).all()

    return productos


@app.get("/productos/destacados", response_model=List[schemas.Producto])
def listar_productos_destacados(db: Session = Depends(get_db)):
    """Retorna solo los productos patrocinados (promotores)."""
    return db.query(models.Producto)\
        .filter(models.Producto.es_patrocinado == True)\
        .order_by(models.Producto.fecha_publicacion.desc())\
        .all()


@app.post("/productos/", response_model=schemas.Producto)
def crear_producto(
    producto: schemas.ProductoCreate,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Crea un nuevo producto en el mercadito. Solo patrocinadores (es_staff) pueden marcar un producto como patrocinado."""
    if producto.categoria not in schemas.CATEGORIAS_VALIDAS:
        raise HTTPException(
            status_code=400,
            detail=f"Categoría inválida. Las categorías válidas son: {', '.join(schemas.CATEGORIAS_VALIDAS)}"
        )

    nuevo_producto = models.Producto(
        titulo=producto.titulo,
        descripcion=producto.descripcion,
        precio=producto.precio,
        categoria=producto.categoria,
        imagen_url=producto.imagen_url,
        es_patrocinado=current_user.es_staff,  # solo staff puede publicar como promotor
        vendedor_id=current_user.id
    )
    db.add(nuevo_producto)
    db.commit()
    db.refresh(nuevo_producto)
    return nuevo_producto


@app.get("/productos/{producto_id}", response_model=schemas.Producto)
def obtener_producto(producto_id: int, db: Session = Depends(get_db)):
    """Obtiene el detalle de un producto por su ID."""
    producto = db.query(models.Producto).filter(models.Producto.id == producto_id).first()
    if not producto:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    return producto


@app.delete("/productos/{producto_id}")
def eliminar_producto(
    producto_id: int,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Elimina un producto si el dueño es el usuario autenticado, o si es staff."""
    producto = db.query(models.Producto).filter(models.Producto.id == producto_id).first()
    if not producto:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    if producto.vendedor_id != current_user.id and not current_user.es_staff:
        raise HTTPException(status_code=403, detail="No tienes permiso para eliminar este producto")
    db.delete(producto)
    db.commit()
    return {"message": "Producto eliminado exitosamente"}


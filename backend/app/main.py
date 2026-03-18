from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from typing import List
from datetime import timedelta
from jose import JWTError, jwt

from . import models, schemas
from .database import engine, get_db
from .auth import verify_password, get_password_hash, create_access_token, ALGORITHM, SECRET_KEY, ACCESS_TOKEN_EXPIRE_MINUTES

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

# Función para obtener el usuario actual mediante el token
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
    """
    Endpoint para registrar un nuevo usuario en el sistema.
    """
    db_user = db.query(models.Usuario).filter(models.Usuario.email == usuario.email).first()
    if db_user:
        raise HTTPException(status_code=400, detail="El email ya está registrado")
    
    hashed_password = get_password_hash(usuario.password)
    
    from datetime import datetime
    
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
    """
    Lista los primeros usuarios registrados.
    """
    usuarios = db.query(models.Usuario).offset(skip).limit(limit).all()
    return usuarios

@app.patch("/usuarios/me", response_model=schemas.Usuario)
def actualizar_perfil(
    datos: schemas.UsuarioUpdate,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Actualiza los datos de configuración del perfil del usuario autenticado.
    Solo se actualizan los campos enviados (PATCH parcial).
    """
    update_data = datos.model_dump(exclude_unset=True)

    # Validar rango de semestre
    if "semestre" in update_data and update_data["semestre"] is not None:
        if not (1 <= update_data["semestre"] <= 9):
            raise HTTPException(status_code=400, detail="El semestre debe estar entre 1 y 9")

    for field, value in update_data.items():
        setattr(current_user, field, value)

    db.commit()
    db.refresh(current_user)
    return current_user

# --- ENDPOINTS: BÚSQUEDA Y PERFILES (NUEVO) ---

@app.get("/usuarios/buscar", response_model=List[schemas.UsuarioResumen])
def buscar_usuarios(q: str, db: Session = Depends(get_db)):
    """
    Busca usuarios por nombre o correo (coincidencia parcial).
    """
    if not q or len(q) < 2:
        return []
    
    termino = f"%{q}%"
    usuarios = db.query(models.Usuario)\
        .filter(models.Usuario.nombre.ilike(termino) | models.Usuario.email.ilike(termino))\
        .limit(20).all()
    return usuarios

@app.get("/usuarios/{usuario_id}", response_model=schemas.Usuario)
def obtener_perfil_usuario(usuario_id: int, db: Session = Depends(get_db)):
    """
    Obtiene el perfil público de un usuario.
    """
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
    """
    Obtiene el historial de publicaciones APROBADAS de un usuario.
    """
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
    """
    Muestra publicaciones APROBADAS del muro. 
    (Si es admin, se podría mostrar todo, pero para feed principal solo aprobadas).
    """
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
    """
    Solo para admins o staff: ver posts pendientes.
    (Por ahora, validamos si es_staff, más adelante se validaría el rol en la comunidad).
    """
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
    # Si es staff o admin, el post se aprueba automáticamente y puede ser oficial
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
    """
    Aprobar o rechazar un post pendiente.
    """
    if not current_user.es_staff:
        raise HTTPException(status_code=403, detail="No tienes permisos para moderar")
        
    pub = db.query(models.PublicacionComunidad).filter(models.PublicacionComunidad.id == pub_id).first()
    if not pub or pub.comunidad_id != comunidad_id:
        raise HTTPException(status_code=404, detail="Publicación no encontrada")
        
    pub.estado = actualizacion.estado.upper()
    db.commit()
    db.refresh(pub)
    return pub

# TODO: Endpoints de Productos (Mercadito), Reportes y Rastreo.
# Quedarán pendientes para la próxima semana.

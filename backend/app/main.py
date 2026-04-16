from fastapi import FastAPI, Depends, HTTPException, status, UploadFile, File
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from sqlalchemy.orm import Session
from typing import List
from datetime import timedelta
from jose import JWTError, jwt
import secrets
import random
import string
import os
import shutil
import uuid

from . import models, schemas
from .database import engine, get_db
from .auth import (
    verify_password, get_password_hash, create_access_token,
    create_reset_token, verify_reset_token,
    ALGORITHM, SECRET_KEY, ACCESS_TOKEN_EXPIRE_MINUTES
)

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

# Funcion para obtener el usuario actual mediante el token
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

os.makedirs("uploads", exist_ok=True)

app = FastAPI(
    title="Comunidad Universitaria API (Preview)",
    description="Backend para la app movil de la universidad - **FASE DE DESARROLLO**",
    version="0.1.0"
)

app.mount("/static", StaticFiles(directory="uploads"), name="static")

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

# --- ENDPOINTS BASICOS: USUARIOS ---

@app.post("/usuarios/", response_model=schemas.Usuario)
def crear_usuario(usuario: schemas.UsuarioCreate, db: Session = Depends(get_db)):
    """
    Endpoint para registrar un nuevo usuario en el sistema.
    """
    db_user = db.query(models.Usuario).filter(models.Usuario.email == usuario.email).first()
    if db_user:
        raise HTTPException(status_code=400, detail="El email ya esta registrado")
    
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
    Actualiza los datos de configuracion del perfil del usuario autenticado.
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

# --- ENDPOINTS: BUSQUEDA Y PERFILES ---

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
    Obtiene el perfil publico de un usuario.
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
        raise HTTPException(status_code=404, detail="Publicacion no encontrada")
        
    pub.estado = actualizacion.estado.upper()
    db.commit()
    db.refresh(pub)
    return pub

# ===================================================
#  MARKETPLACE (Productos)
# ===================================================

@app.get("/productos/", response_model=List[schemas.Producto])
def listar_productos(
    skip: int = 0,
    limit: int = 20,
    categoria: str = None,
    q: str = None,
    db: Session = Depends(get_db)
):
    """
    Lista productos activos. Filtra por categoria y/o busqueda de texto.
    """
    query = db.query(models.Producto).filter(models.Producto.estado == "activo")
    
    if categoria:
        query = query.filter(models.Producto.categoria == categoria)
    
    if q and len(q) >= 2:
        termino = f"%{q}%"
        query = query.filter(
            models.Producto.titulo.ilike(termino) | 
            models.Producto.descripcion.ilike(termino)
        )
    
    productos = query.order_by(models.Producto.fecha_creacion.desc())\
        .offset(skip).limit(limit).all()
    return productos

@app.get("/productos/mis-productos", response_model=List[schemas.Producto])
def mis_productos(
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Lista todos los productos del usuario autenticado.
    """
    productos = db.query(models.Producto)\
        .filter(models.Producto.vendedor_id == current_user.id)\
        .order_by(models.Producto.fecha_creacion.desc())\
        .all()
    return productos

@app.get("/productos/{producto_id}", response_model=schemas.Producto)
def obtener_producto(producto_id: int, db: Session = Depends(get_db)):
    producto = db.query(models.Producto).filter(models.Producto.id == producto_id).first()
    if not producto:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    return producto

@app.post("/productos/", response_model=schemas.Producto)
def crear_producto(
    producto: schemas.ProductoCreate,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    categorias_validas = ["libros", "electronicos", "ropa", "accesorios", "otros"]
    if producto.categoria not in categorias_validas:
        raise HTTPException(status_code=400, detail=f"Categoria invalida. Usa: {', '.join(categorias_validas)}")
    
    nuevo_producto = models.Producto(
        titulo=producto.titulo,
        descripcion=producto.descripcion,
        precio=producto.precio,
        categoria=producto.categoria,
        condicion=producto.condicion,
        vendedor_id=current_user.id
    )
    db.add(nuevo_producto)
    db.commit()
    db.refresh(nuevo_producto)
    return nuevo_producto

@app.patch("/productos/{producto_id}", response_model=schemas.Producto)
def actualizar_producto(
    producto_id: int,
    datos: schemas.ProductoUpdate,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    producto = db.query(models.Producto).filter(models.Producto.id == producto_id).first()
    if not producto:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    if producto.vendedor_id != current_user.id:
        raise HTTPException(status_code=403, detail="Solo el vendedor puede editar este producto")
    
    update_data = datos.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(producto, field, value)
    
    db.commit()
    db.refresh(producto)
    return producto

@app.delete("/productos/{producto_id}")
def eliminar_producto(
    producto_id: int,
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    producto = db.query(models.Producto).filter(models.Producto.id == producto_id).first()
    if not producto:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    if producto.vendedor_id != current_user.id:
        raise HTTPException(status_code=403, detail="Solo el vendedor puede eliminar este producto")
    
    db.delete(producto)
    db.commit()
    return {"mensaje": "Producto eliminado correctamente"}

@app.post("/productos/{producto_id}/imagen", response_model=schemas.Producto)
def upload_imagen_producto(
    producto_id: int,
    file: UploadFile = File(...),
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Sube una imagen fisica para el producto. (Solo admin o propietario).
    """
    producto = db.query(models.Producto).filter(models.Producto.id == producto_id).first()
    if not producto:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    if producto.vendedor_id != current_user.id and not current_user.es_staff:
        raise HTTPException(status_code=403, detail="No puedes modificar este producto")

    # Extension del archivo
    ext = file.filename.split(".")[-1] if "." in file.filename else "jpg"
    new_filename = f"{uuid.uuid4().hex}.{ext}"
    filepath = os.path.join("uploads", new_filename)

    # Borrar la vieja si existe
    if producto.imagen_url:
        old_path = os.path.join("uploads", os.path.basename(producto.imagen_url))
        if os.path.exists(old_path):
            os.remove(old_path)

    # Escribir a disco
    with open(filepath, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    producto.imagen_url = f"/static/{new_filename}"
    db.commit()
    db.refresh(producto)

    return producto

# ===================================================
#  RECUPERACION DE CUENTA (Recovery System)
# ===================================================

# Almacen temporal en memoria para codigos de verificacion por email
# En produccion esto seria Redis o similar
_email_verification_codes: dict = {}

@app.post("/recovery/generate-codes", response_model=schemas.RecoveryCodesResponse)
def generar_codigos_respaldo(
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Genera 8 codigos de respaldo para el usuario autenticado.
    Los codigos solo se muestran UNA VEZ en texto plano.
    Internamente se guardan hasheados.
    """
    # Borrar codigos anteriores
    db.query(models.CodigoRecuperacion)\
        .filter(models.CodigoRecuperacion.usuario_id == current_user.id)\
        .delete()
    
    codigos_planos = []
    for _ in range(8):
        parte1 = ''.join(random.choices(string.ascii_uppercase + string.digits, k=4))
        parte2 = ''.join(random.choices(string.ascii_uppercase + string.digits, k=4))
        codigo = f"{parte1}-{parte2}"
        codigos_planos.append(codigo)
        
        codigo_db = models.CodigoRecuperacion(
            usuario_id=current_user.id,
            codigo_hash=get_password_hash(codigo),
            usado=False
        )
        db.add(codigo_db)
    
    db.commit()
    
    print(f"\n{'='*50}")
    print(f"CODIGOS DE RESPALDO generados para: {current_user.email}")
    print(f"{'='*50}")
    for i, c in enumerate(codigos_planos, 1):
        print(f"   {i}. {c}")
    print(f"{'='*50}\n")
    
    return schemas.RecoveryCodesResponse(
        codigos=codigos_planos,
        mensaje="Guarda estos codigos en un lugar seguro. No se mostraran de nuevo.",
        total=8
    )

@app.get("/recovery/codes-status", response_model=schemas.RecoveryCodesStatus)
def estado_codigos_respaldo(
    current_user: models.Usuario = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Devuelve cuantos codigos de respaldo tiene el usuario y cuantos quedan sin usar.
    """
    total = db.query(models.CodigoRecuperacion)\
        .filter(models.CodigoRecuperacion.usuario_id == current_user.id)\
        .count()
    disponibles = db.query(models.CodigoRecuperacion)\
        .filter(models.CodigoRecuperacion.usuario_id == current_user.id)\
        .filter(models.CodigoRecuperacion.usado == False)\
        .count()
    return schemas.RecoveryCodesStatus(total=total, disponibles=disponibles)

@app.post("/recovery/request-email", response_model=schemas.ResetTokenResponse)
def solicitar_codigo_por_email(
    datos: schemas.RecoveryRequestEmail,
    db: Session = Depends(get_db)
):
    """
    Envia un codigo de 6 digitos al correo de recuperacion del usuario.
    En desarrollo: el codigo se imprime en la consola del backend.
    """
    user = db.query(models.Usuario).filter(models.Usuario.email == datos.email).first()
    if not user:
        raise HTTPException(status_code=200, detail="Si el correo existe, se enviara un codigo de recuperacion")
    
    if not user.correo_recuperacion:
        raise HTTPException(
            status_code=400, 
            detail="Esta cuenta no tiene un correo de recuperacion configurado"
        )
    
    # Generar codigo de 6 digitos
    codigo = ''.join(random.choices(string.digits, k=6))
    
    # Guardar en memoria temporal
    _email_verification_codes[datos.email] = {
        "code": codigo,
        "correo_destino": user.correo_recuperacion
    }
    
    # SIMULACION: imprimir en consola
    print(f"\n{'='*50}")
    print(f"CODIGO DE RECUPERACION para: {datos.email}")
    print(f"   Enviado a: {user.correo_recuperacion}")
    print(f"   Codigo: {codigo}")
    print(f"   (Expira en 15 minutos)")
    print(f"{'='*50}\n")
    
    # Enmascarar el correo de recuperacion para la respuesta
    email_parts = user.correo_recuperacion.split("@")
    masked = email_parts[0][:2] + "***@" + email_parts[1]
    
    return schemas.ResetTokenResponse(
        reset_token="",
        mensaje=f"Se envio un codigo de verificacion a {masked}"
    )

@app.post("/recovery/verify-email-code", response_model=schemas.ResetTokenResponse)
def verificar_codigo_email(
    datos: schemas.RecoveryVerifyEmail,
    db: Session = Depends(get_db)
):
    """
    Verifica el codigo de 6 digitos enviado al correo secundario.
    Si es correcto, devuelve un reset_token temporal (15 min).
    """
    stored = _email_verification_codes.get(datos.email)
    if not stored or stored["code"] != datos.codigo:
        raise HTTPException(status_code=400, detail="Codigo incorrecto o expirado")
    
    # Limpiar codigo usado
    del _email_verification_codes[datos.email]
    
    # Generar reset token
    reset_token = create_reset_token(datos.email)
    
    return schemas.ResetTokenResponse(
        reset_token=reset_token,
        mensaje="Codigo verificado. Ahora puedes cambiar tu contrasena."
    )

@app.post("/recovery/verify-backup-code", response_model=schemas.ResetTokenResponse)
def verificar_codigo_respaldo(
    datos: schemas.RecoveryVerifyBackup,
    db: Session = Depends(get_db)
):
    """
    Verifica un codigo de respaldo (backup code).
    Si es correcto, lo marca como usado y devuelve un reset_token temporal.
    """
    user = db.query(models.Usuario).filter(models.Usuario.email == datos.email).first()
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    # Buscar entre los codigos no usados
    codigos = db.query(models.CodigoRecuperacion)\
        .filter(models.CodigoRecuperacion.usuario_id == user.id)\
        .filter(models.CodigoRecuperacion.usado == False)\
        .all()
    
    codigo_encontrado = None
    for c in codigos:
        if verify_password(datos.codigo.upper().strip(), c.codigo_hash):
            codigo_encontrado = c
            break
    
    if not codigo_encontrado:
        raise HTTPException(status_code=400, detail="Codigo de respaldo incorrecto o ya usado")
    
    # Marcar como usado
    codigo_encontrado.usado = True
    db.commit()
    
    # Contar cuantos quedan
    restantes = db.query(models.CodigoRecuperacion)\
        .filter(models.CodigoRecuperacion.usuario_id == user.id)\
        .filter(models.CodigoRecuperacion.usado == False)\
        .count()
    
    print(f"\nCodigo de respaldo USADO por: {user.email} (quedan {restantes} disponibles)\n")
    
    reset_token = create_reset_token(datos.email)
    
    return schemas.ResetTokenResponse(
        reset_token=reset_token,
        mensaje=f"Codigo verificado. Te quedan {restantes} codigos de respaldo."
    )

@app.post("/recovery/reset-password")
def restablecer_contrasena(
    datos: schemas.ResetPasswordRequest,
    db: Session = Depends(get_db)
):
    """
    Restablece la contrasena usando un reset_token valido.
    """
    email = verify_reset_token(datos.reset_token)
    if not email:
        raise HTTPException(status_code=400, detail="Token expirado o invalido. Solicita uno nuevo.")
    
    if len(datos.nueva_password) < 8:
        raise HTTPException(status_code=400, detail="La contrasena debe tener minimo 8 caracteres")
    
    user = db.query(models.Usuario).filter(models.Usuario.email == email).first()
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    user.hashed_password = get_password_hash(datos.nueva_password)
    db.commit()
    
    print(f"\nContrasena restablecida para: {email}\n")
    
    return {"mensaje": "Contrasena actualizada correctamente. Ya puedes iniciar sesion."}

# TODO: Endpoints de Productos (Mercadito), Reportes y Rastreo.

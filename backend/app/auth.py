from datetime import datetime, timedelta
from typing import Optional
from passlib.context import CryptContext
from jose import JWTError, jwt

# Configuración de seguridad (TODO: mover a variables de entorno)
SECRET_KEY = "super_secreto_key_para_desarrollo" # ¡Cambiar en producción!
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24 * 7 # 7 días para "Recordarme"
RESET_TOKEN_EXPIRE_MINUTES = 15  # 15 minutos para restablecer contraseña

pwd_context = CryptContext(schemes=["pbkdf2_sha256"], deprecated="auto")

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password):
    return pwd_context.hash(password)

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def create_reset_token(email: str) -> str:
    """Crea un token temporal para restablecer contraseña (15 min de vida)."""
    expire = datetime.utcnow() + timedelta(minutes=RESET_TOKEN_EXPIRE_MINUTES)
    to_encode = {"sub": f"reset:{email}", "exp": expire, "type": "reset"}
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def verify_reset_token(token: str) -> Optional[str]:
    """Verifica un reset token y devuelve el email si es válido, None si no."""
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        sub = payload.get("sub", "")
        token_type = payload.get("type", "")
        if token_type != "reset" or not sub.startswith("reset:"):
            return None
        return sub.replace("reset:", "")
    except JWTError:
        return None


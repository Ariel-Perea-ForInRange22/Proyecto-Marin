"""
Script de prueba del backend DevCore UAT
Simula exactamente las llamadas que haría la app Android:
  1. Login         -> POST /login
  2. Ver perfil    -> GET  /usuarios/me
  3. Actualizar    -> PATCH /usuarios/me  (correo, fecha, semestre, grupo, huella)
  4. Verificar     -> GET  /usuarios/me   (confirmar que se guardo)
  5. Error control -> PATCH semestre invalido (debe dar 400)
"""

import urllib.request
import urllib.parse
import json
import sys

BASE = "http://127.0.0.1:8000"
EMAIL = "a0257347588@uat.edu.mx"
PASSWORD = "password123"

def ok(msg):   print(f"  [OK] {msg}")
def fail(msg): print(f"  [FAIL] {msg}"); sys.exit(1)
def info(msg): print(f"       {msg}")
def step(msg): print(f"\n>> {msg}")

def http(method, path, data=None, token=None):
    url = BASE + path
    body = json.dumps(data).encode() if data else None
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as r:
            return r.status, json.loads(r.read())
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read())

def http_form(path, fields):
    url = BASE + path
    body = urllib.parse.urlencode(fields).encode()
    req = urllib.request.Request(url, data=body,
          headers={"Content-Type": "application/x-www-form-urlencoded"}, method="POST")
    try:
        with urllib.request.urlopen(req) as r:
            return r.status, json.loads(r.read())
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read())

print("\n" + "="*55)
print(" PRUEBAS BACKEND DevCore UAT")
print("="*55)

# 0. Health check
step("0. Health check del servidor")
code, body = http("GET", "/")
if code == 200:
    ok(f"Servidor OK -> {body.get('message', body)}")
else:
    fail(f"Servidor no responde (HTTP {code})")

# 1. Login
step("1. Login - POST /login")
info(f"Usuario: {EMAIL}")
code, body = http_form("/login", {"username": EMAIL, "password": PASSWORD})
if code == 200 and "access_token" in body:
    TOKEN = body["access_token"]
    ok("JWT obtenido correctamente")
    info(f"Token: {TOKEN[:50]}...")
else:
    fail(f"Login fallo (HTTP {code}): {body}")

# 2. Perfil actual
step("2. Ver perfil actual - GET /usuarios/me")
code, user = http("GET", "/usuarios/me", token=TOKEN)
if code == 200:
    ok(f"Perfil: {user['nombre']} <{user['email']}>")
    info(f"correo_recuperacion : {user.get('correo_recuperacion') or '(vacio)'}")
    info(f"fecha_nacimiento    : {user.get('fecha_nacimiento') or '(vacio)'}")
    info(f"semestre            : {user.get('semestre') or '(vacio)'}")
    info(f"grupo               : {user.get('grupo') or '(vacio)'}")
    info(f"huella_habilitada   : {user.get('huella_habilitada')}")
else:
    fail(f"No se pudo cargar perfil (HTTP {code}): {user}")

# 3. Guardar configuracion
step("3. Guardar configuracion - PATCH /usuarios/me")
payload = {
    "correo_recuperacion": "recuperacion.prueba@gmail.com",
    "fecha_nacimiento": "2002-06-15",
    "semestre": 6,
    "grupo": "A",
    "huella_habilitada": True
}
info(f"Payload: {json.dumps(payload, ensure_ascii=False)}")
code, updated = http("PATCH", "/usuarios/me", data=payload, token=TOKEN)
if code == 200:
    ok("Configuracion guardada!")
    info(f"correo_recuperacion : {updated.get('correo_recuperacion')}")
    info(f"fecha_nacimiento    : {updated.get('fecha_nacimiento')}")
    info(f"semestre            : {updated.get('semestre')}")
    info(f"grupo               : {updated.get('grupo')}")
    info(f"huella_habilitada   : {updated.get('huella_habilitada')}")
else:
    fail(f"Error al guardar (HTTP {code}): {updated}")

# 4. Verificar persistencia
step("4. Verificar persistencia - segunda lectura")
code, verified = http("GET", "/usuarios/me", token=TOKEN)
if code == 200:
    errs = []
    if verified.get("correo_recuperacion") != "recuperacion.prueba@gmail.com": errs.append("correo_recuperacion")
    if verified.get("fecha_nacimiento") != "2002-06-15": errs.append("fecha_nacimiento")
    if verified.get("semestre") != 6: errs.append("semestre")
    if verified.get("grupo") != "A": errs.append("grupo")
    if verified.get("huella_habilitada") is not True: errs.append("huella_habilitada")
    if errs:
        fail("Campos no persistidos: " + ", ".join(errs))
    else:
        ok("Todos los campos persisten correctamente en la BD!")
else:
    fail(f"Error al verificar (HTTP {code}): {verified}")

# 5. Validacion semestre invalido
step("5. Validacion de errores - semestre=15 (debe dar HTTP 400)")
code, err_body = http("PATCH", "/usuarios/me", data={"semestre": 15}, token=TOKEN)
if code == 400:
    ok(f"Backend rechazo correctamente HTTP 400: '{err_body.get('detail')}'")
else:
    fail(f"Se esperaba HTTP 400, llego HTTP {code}: {err_body}")

# Resultado
print("\n" + "="*55)
print("  TODAS LAS PRUEBAS PASARON EXITOSAMENTE!")
print("="*55 + "\n")

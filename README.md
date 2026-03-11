# DevCore UAT 📱
### Plataforma Universitaria Integral — Universidad Autónoma de Tamaulipas

---

## 🎯 Descripción

**DevCore UAT** es una aplicación móvil Android nativa desarrollada para estudiantes de la UAT. Integra múltiples servicios universitarios en una sola plataforma: seguimiento de transporte, mercado estudiantil, comunidades y un sistema de gamificación basado en reputación.

**Estado actual:** `Módulo 1 — Arquitectura y Contrato Técnico (Semana 1) ✅`

---

## 🗂️ Estructura del Proyecto

```
Proyecto-Marin/
├── kotlin/                          # App Android nativa (Kotlin)
│   └── app/src/main/
│       ├── java/com/devcore/uat/     # Actividades y lógica
│       │   ├── SplashActivity.kt
│       │   ├── LoginActivity.kt
│       │   ├── RegisterActivity.kt
│       │   ├── PendingActivity.kt
│       │   ├── HomeActivity.kt
│       │   ├── BusTrackingActivity.kt
│       │   ├── MarketplaceActivity.kt
│       │   ├── ProfileActivity.kt
│       │   └── ComunidadesActivity.kt
│       └── res/                     # Layouts, drawables, valores
├── backend/                         # API REST en Python
│   └── app/
│       ├── main.py                  # Endpoints FastAPI
│       ├── models.py                # Modelos SQLAlchemy (BD)
│       ├── auth.py                  # JWT + hashing
│       ├── database.py              # Conexión SQLite
│       └── schemas.py               # Validación Pydantic
├── frontend_web_legacy/             # Prototipo HTML original (referencia)
├── DOCUMENTACION_PROYECTO.md        # Épica v2.0 completa
├── ESTADO_PROYECTO_SEMANA1.docx     # Entregable Semana 1
└── README.md
```

---

## 📱 Pantallas Implementadas

| Pantalla | Descripción | Estado |
|---|---|---|
| Bienvenida (Splash) | Pantalla de inicio con logo UAT | ✅ |
| Inicio de Sesión | Autenticación con JWT | ✅ |
| Registro | Validación de correo `@uat.edu.mx` | ✅ |
| Cuenta Pendiente | Espera de aprobación administrativa | ✅ |
| Home (Dashboard) | Stats del usuario + acceso a módulos | ✅ |
| Seguimiento de Bus | Selección de rutas y reportes colaborativos | ✅ |
| Mercado Estudiantil | Compra-venta entre estudiantes | ✅ |
| Mi Perfil | Nivel de confianza, rachas y logros | ✅ |
| Comunidades | Grupos estudiantiles por facultad/interés | ✅ |

---

## 🗄️ Base de Datos (SQLite)

Esquema diseñado con **6 tablas** y relaciones many-to-many:

```
usuarios ──── usuario_comunidad ──── comunidades
    │
    └──────── usuario_curso ─────── cursos
```

**Tablas activas:**
- `usuarios` — Datos de cuenta, nivel de confianza, rachas
- `comunidades` — Grupos estudiantiles
- `cursos` — Materias universitarias
- `usuario_comunidad` — Relación usuario ↔ comunidad
- `usuario_curso` — Relación usuario ↔ materia
- `productos` — Artículos del marketplace

> La base de datos viene con **21 usuarios de prueba** con formato `a##########@uat.edu.mx` y contraseña `password123`.

---

## ⚙️ Backend (FastAPI)

**Endpoints disponibles:**

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Status del servidor |
| `POST` | `/usuarios/` | Registrar usuario |
| `POST` | `/login` | Login → devuelve JWT |
| `GET` | `/usuarios/me` | Perfil autenticado |
| `GET` | `/usuarios/` | Listar usuarios |

### Cómo levantar el servidor

```bash
cd backend
python -m venv venv
.\venv\Scripts\activate       # Windows
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

> La app Android se conecta automáticamente al host configurado en `RetrofitClient.kt`.

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología |
|---|---|
| App Móvil | Kotlin + ViewBinding + AppCompat |
| HTTP Client | Retrofit2 + OkHttp |
| Backend | Python 3 + FastAPI + Uvicorn |
| ORM | SQLAlchemy |
| Base de Datos | SQLite → PostgreSQL (producción) |
| Autenticación | JWT + PBKDF2-SHA256 |
| Almacenamiento sesión | Jetpack DataStore |

---

## 🎨 Paleta de Colores UAT (Pastel)

| Color | Hex | Uso |
|---|---|---|
| Naranja Pastel | `#FFB39C` | Botones primarios, acentos |
| Azul Pastel | `#8AB4D7` | Títulos, cabeceras |
| Gris Pastel | `#B8BCC2` | Texto secundario |
| Verde Pastel | `#81C784` | Estados positivos |
| Rojo Pastel | `#EF9A9A` | Errores, alertas |

---

## 📅 Módulos del Proyecto

| Módulo | Contenido | Estado |
|---|---|---|
| **1 — Arquitectura y Contrato Técnico** | BD, Mapa de Navegación, Épica v2.0 | ✅ Semana 1 |
| **2 — Onboarding y Seguridad** | Registro, Cloud Storage, Restricción de Acceso | 🔲 Semana 2 |
| **3 — Administración y Gestión de Acceso** | Panel Admin, Gestión de Documentos, Roles | 🔲 Semana 3 |
| **4 — Núcleo Social y Reportes** | Feed Dinámico, Bus Tracking, Interacción Social | 🔲 Semana 4 |
| **5 — Marketplace, Reputación y Gamificación** | Catálogo, Sistema de Reputación, Monetización | 🔲 Semana 5 |

---

## 👥 Equipo

**Cliente:** Workshop Discovery Value  
**Universidad:** Universidad Autónoma de Tamaulipas  
**Fecha de inicio:** Febrero 2026

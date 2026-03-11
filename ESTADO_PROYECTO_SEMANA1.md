# 📱 DevCore UAT — Estado del Proyecto
**Fecha de Revisión:** Marzo 2026 | **Sprint Actual:** Semana 1 — Arquitectura y Contrato Técnico

---

## 🎯 Resumen Ejecutivo

DevCore UAT es una plataforma integral para estudiantes de la **Universidad Autónoma de Tamaulipas (UAT)**. El proyecto ha avanzado desde un prototipo de HTML estático a una aplicación Android nativa con backend real, base de datos estructurada y comunicación cliente-servidor funcional.

---

## ✅ Logros de Esta Semana

### 1. Aplicación Android (Kotlin + Jetpack/ViewBinding)
Se cuenta con **9 pantallas funcionales** compiladas y corriendo en dispositivo físico:

| Pantalla | Archivo | Estado |
|---|---|---|
| Pantalla de Bienvenida | `SplashActivity.kt` | ✅ Funcional |
| Inicio de Sesión | `LoginActivity.kt` | ✅ Conectado al backend |
| Registro | `RegisterActivity.kt` | ✅ Funcional |
| Cuenta Pendiente | `PendingActivity.kt` | ✅ Funcional |
| Inicio (Home) | `HomeActivity.kt` | ✅ Funcional |
| Seguimiento de Bus | `BusTrackingActivity.kt` | ✅ UI lista |
| Mercado Estudiantil | `MarketplaceActivity.kt` | ✅ UI lista |
| Mi Perfil | `ProfileActivity.kt` | ✅ Conectado al backend |
| Comunidades | `ComunidadesActivity.kt` | ✅ **Nuevo esta semana** |

**Navegación:** Todas las pantallas están conectadas mediante `Intent` de Android. La barra de navegación inferior cuenta con 5 destinos: Inicio, Bus, Mercado, Comunidades y Perfil.

---

### 2. Backend (Python — FastAPI)
Servidor REST funcional corriendo en la red local del equipo de desarrollo.

- **Framework:** FastAPI con SQLAlchemy ORM
- **Base de datos:** SQLite (`sql_app.db`) — desarrollo local
- **Autenticación:** JWT (Tokens de sesión) + Hashing de contraseñas (PBKDF2-SHA256)
- **Puerto:** `8000` — accesible desde dispositivos en la misma red WiFi

**Endpoints activos:**

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Estado del servidor |
| `POST` | `/usuarios/` | Registrar nuevo usuario |
| `POST` | `/login` | Inicio de sesión (devuelve JWT) |
| `GET` | `/usuarios/me` | Perfil del usuario autenticado |
| `GET` | `/usuarios/` | Listar usuarios (admin) |

---

### 3. Diseño de Base de Datos — Relación Usuarios-Comunidades-Cursos

> **Este es el entregable clave de la Semana 1.**

Se diseñaron e implementaron **6 tablas** en SQLite con sus relaciones:

```
usuarios ──────────────── usuario_comunidad ─── comunidades
    │                                                
    └────────────────────── usuario_curso ──────── cursos

usuarios ──── productos (Marketplace — base)
```

#### Tabla `usuarios`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Integer PK | Identificador único |
| `nombre` | String | Nombre completo |
| `email` | String (unique) | Correo `a##########@uat.edu.mx` |
| `hashed_password` | String | Contraseña encriptada (PBKDF2) |
| `nivel_confianza` | Integer | Puntos de reputación (0-100) |
| `racha_diaria` | Integer | Días consecutivos activos |
| `ultima_conexion` | DateTime | Timestamp de última actividad |

#### Tabla `comunidades`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Integer PK | Identificador |
| `nombre` | String (unique) | Nombre de la comunidad |
| `descripcion` | String | Descripción breve |
| `fecha_creacion` | DateTime | Cuándo fue creada |

#### Tabla `cursos`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Integer PK | Identificador |
| `nombre` | String | Nombre de la materia |
| `codigo` | String (unique) | Clave (ej. MAT-101) |
| `creditos` | Integer | Valor en créditos |

#### Tablas de Relación (Many-to-Many)
- **`usuario_comunidad`**: Conecta usuarios con sus comunidades. Campos: `usuario_id`, `comunidad_id`, `fecha_union`.
- **`usuario_curso`**: Conecta usuarios con sus materias inscritas. Campos: `usuario_id`, `curso_id`, `fecha_inscripcion`.

#### Tabla `productos` (Base del Marketplace)
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Integer PK | Identificador |
| `titulo` | String | Nombre del artículo |
| `descripcion` | String | Descripción del artículo |
| `precio` | Float | Precio en MXN |
| `vendedor_id` | Integer FK | Referencia al `usuario` que vende |

> **Datos de prueba:** La base de datos cuenta con **21 usuarios de prueba** con correos institucionales del formato `a##########@uat.edu.mx`.

---

## ⏳ Pendientes para Próximas Semanas

### Semana 2 — Funcionalidades Core
- [ ] Funcionalidad completa de Comunidades (crear grupo, unirse, publicar)
- [ ] Pantalla de Cursos (inscripción, ver horario)
- [ ] Upload de ficha de pago al registrarse
- [ ] Panel administrativo para aprobación de cuentas

### Semana 3 — Marketplace y Bus
- [ ] CRUD completo de productos en el Marketplace
- [ ] Sistema de reportes del bus con categorías
- [ ] Chat entre comprador y vendedor

### Semana 4 — Sistema de Reputación y Gamificación
- [ ] Cálculo automático de `nivel_confianza`
- [ ] Sistema de logros/badges (Tabla `logros_usuarios`)
- [ ] Registro de movimientos de confianza (Tabla `registro_confianza`)

### Largo Plazo
- [ ] Integración con Google Maps API para el Bus Tracking en tiempo real
- [ ] Notificaciones push
- [ ] Migración de SQLite a PostgreSQL para producción
- [ ] Deploy en servidor productivo
- [ ] Chat en tiempo real (WebSockets)

---

## 📊 Métricas del Proyecto

| Métrica | Valor |
|---|---|
| Pantallas Android | 9 |
| Archivos Kotlin | 10 |
| Tablas en BD | 6 (activas) |
| Endpoints REST | 5 |
| Usuarios de prueba | 21 |
| Sprints completados | 1 de 4 |

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología |
|---|---|
| **App Móvil** | Kotlin + ViewBinding + AppCompat |
| **Comunicación HTTP** | Retrofit2 + OkHttp |
| **Backend API** | Python 3 + FastAPI + Uvicorn |
| **ORM** | SQLAlchemy |
| **Base de Datos** | SQLite (desarrollo) → PostgreSQL (producción) |
| **Autenticación** | JWT (python-jose) + PBKDF2-SHA256 |
| **Seguridad** | Tokens de sesión almacenados con DataStore |

---

*Documento generado el 11 de Marzo de 2026 — DevCore UAT v1.0 (Semana 1)*

# DevCore - Workshop Discovery Value
## Proyecto Universidad Autónoma de Tamaulipas (UAT)

Este proyecto contiene la **interfaz visual completa** de la plataforma móvil DevCore según las especificaciones del documento de requerimientos.

---

## 📋 Descripción

DevCore es una plataforma universitaria integral que incluye:
- Sistema de validación de identidad institucional
- Módulo de rastreo de buses en tiempo real
- Sistema de gamificación y reputación
- Marketplace para compra/venta de artículos

---

## 🎨 Colores UAT - Paleta Pastel

- **Naranja Pastel:** #FFB39C
- **Naranja Hover:** #FF9F85
- **Azul Pastel:** #8AB4D7
- **Azul Hover:** #6FA3CC
- **Gris Pastel:** #B8BCC2
- **Verde Pastel:** #81C784 (botones success)
- **Rojo Pastel:** #EF9A9A (botones danger)
- **Amarillo Pastel:** #FFD97D (alertas)

---

## 📱 Páginas Incluidas

### 1. **index.html** - Pantalla de Bienvenida
- Logo de la aplicación
- Botones de "Iniciar Sesión" y "Registrarse"

### 2. **login.html** - Inicio de Sesión
- Formulario de correo institucional
- Campo de contraseña
- Enlace de recuperación de contraseña

### 3. **register.html** - Registro
- Formulario completo de registro
- Validación de correo institucional (@uat.edu.mx)
- Carga obligatoria de ficha de pago
- Términos y condiciones

### 4. **pending.html** - Estado Pendiente
- Pantalla de espera durante validación administrativa
- Indicador de progreso
- Estado de verificación de documentos

### 5. **home.html** - Dashboard Principal
- Vista de nivel de confianza y rachas
- Acceso rápido a todos los módulos
- Notificaciones recientes
- Barra de navegación inferior

### 6. **bus-tracking.html** - Rastreo de Buses
- Mapa de rutas (placeholder visual)
- Selector de rutas
- Botones "Ya pasó" / "No ha pasado"
- Reportes recientes de otros usuarios
- Aviso de privacidad

### 7. **profile.html** - Perfil de Usuario
- Nivel de confianza con barra de progreso
- Sistema de rachas (engagement)
- Logros desbloqueados
- Historial de actividad
- Opciones de configuración

### 8. **marketplace.html** - Marketplace
- Buscador de productos
- Categorías (Libros, Electrónicos, Ropa, Accesorios)
- Grid de productos con imágenes
- Sistema de favoritos (corazón)
- Mis productos publicados
- Botón flotante para agregar productos

---

## 🚀 Cómo Visualizar

1. Abre el archivo `index.html` en tu navegador web
2. Navega entre las diferentes pantallas usando los botones
3. La interfaz está optimizada para dispositivos móviles (max-width: 480px)

---

## ✨ Características Visuales

- ✅ Diseño Mobile First responsive
- ✅ **Paleta de colores pasteles suaves** (no colores sólidos)
- ✅ Gradientes con colores UAT en tonos pastel
- ✅ Navegación intuitiva con botones de regreso
- ✅ Barra de navegación inferior fija
- ✅ Animaciones y transiciones suaves
- ✅ Sistema de badges y alertas
- ✅ Barras de progreso
- ✅ Cards con efectos hover
- ✅ Íconos con emojis

---

## 📝 Notas Importantes
 (prototipo/mockup)**

**Lo que SÍ tiene:**
- ✅ Diseño completo de todas las pantallas
- ✅ Navegación entre páginas HTML
- ✅ Paleta de colores pastel inspirada en UAT
- ✅ Flujos de usuario definidos
- ✅ Estructura visual de todos los módulos

**Lo que NO tiene (requiere implementación):**
- ❌ **Frontend en Kotlin:** La interfaz actual es HTML/CSS (web), necesitas recrearla en Kotlin para Android nativo
- ❌ **Backend en Python:** No hay lógica de servidor, base de datos, APIs ni autenticación
- ❌ **Funcionalidad real:** Los botones solo navegan entre páginas, no hay validaciones ni procesamiento
- ❌ **Geolocalización:** El módulo de Bus Tracking es solo visual
- ❌ **Seguridad:** No hay encriptación ni validación de dominio institucional (@uat.edu.mx)

**Uso recomendado:**
- 📱 Usar como **prototipo de referencia** para desarrollar la app nativa en Kotlin
- 🐍 Implementar el backend en Python (Flask/Django/FastAPI) basándose en estos flujos
- 🎨 Usar esta paleta de colores pastel en la app final
- 📋 Seguir la estructura y flujo de navegación propuestosatos
- No hay geolocalización real

---

## 👥 Equipo DevCore

**Cliente:** Workshop Discovery Value  
**Fecha:** Febrero 2026  
**Universidad:** Universidad Autónoma de Tamaulipas

---

## 📄 Archivos del Proyecto

```
Proyecto_Marin/
├── index.html           (Splash/Bienvenida)
├── login.html          (Inicio de Sesión)
├── register.html       (Registro)
├── pending.html        (Estado Pendiente)
├── home.html           (Dashboard)
├── bus-tracking.html   (Rastreo de Buses)
├── profile.html        (Perfil y Gamificación)
├── marketplace.html    (Marketplace)
├── styles.css          (Estilos Globales)
└── README.md           (Este archivo)
```

---

## 🎯 Módulos por Semana (Según Especificaciones)

- **Semana 1:** Seguridad y Onboarding ✅
- **Semana 2:** Bus Tracking ✅
- **Semana 3:** Gamificación y Reputación ✅
- **Semana 4:** Marketplace ✅

---

¡Interfaz visual completada! 🎉

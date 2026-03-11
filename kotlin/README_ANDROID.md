# DevCore - Aplicación Android UAT

Migración del diseño web legacy a una aplicación nativa Android usando Kotlin.

## 📱 Descripción

DevCore es una plataforma universitaria integral para la Universidad Autónoma de Tamaulipas que incluye:
- **Bus Tracking**: Seguimiento en tiempo real del transporte universitario
- **Marketplace**: Compra y venta de artículos universitarios
- **Sistema de Reputación**: Nivel de confianza y rachas de engagement
- **Autenticación**: Login y registro con validación institucional

## 🎨 Diseño

La aplicación mantiene **fidelidad visual** al diseño web original:
- **Colores UAT**: Paleta pastel (#FFB39C, #8AB4D7, #B8BCC2)
- **Gradientes**: Fondos degradados en headers y cards
- **Tipografía**: Sans-serif con diferentes pesos
- **Espaciado**: Consistente con el diseño web (8dp, 16dp, 24dp)

## 📂 Estructura del Proyecto

```
app/src/main/
├── java/com/devcore/
│   ├── SplashActivity.kt          # Pantalla inicial
│   ├── LoginActivity.kt           # Inicio de sesión
│   ├── RegisterActivity.kt        # Registro de usuario
│   ├── PendingActivity.kt         # Estado de cuenta pendiente
│   ├── HomeActivity.kt            # Pantalla principal
│   ├── BusTrackingActivity.kt     # Tracking de buses (stub)
│   ├── ProfileActivity.kt         # Perfil de usuario (stub)
│   └── MarketplaceActivity.kt     # Marketplace (stub)
│
├── res/
│   ├── layout/
│   │   ├── activity_splash.xml
│   │   ├── activity_login.xml
│   │   ├── activity_register.xml
│   │   ├── activity_pending.xml
│   │   ├── activity_home.xml
│   │   └── bottom_navigation.xml
│   │
│   ├── drawable/
│   │   ├── gradient_splash.xml
│   │   ├── gradient_header.xml
│   │   ├── gradient_stats.xml
│   │   ├── gradient_marketplace.xml
│   │   ├── gradient_profile.xml
│   │   ├── bg_circle_white.xml
│   │   ├── bg_circle_gradient.xml
│   │   ├── bg_back_button.xml
│   │   ├── bg_file_upload.xml
│   │   ├── badge_success.xml
│   │   └── badge_secondary.xml
│   │
│   └── values/
│       ├── colors.xml             # Paleta UAT completa
│       ├── themes.xml             # Estilos globales
│       ├── dimens.xml             # Dimensiones consistentes
│       └── strings.xml            # Textos de la app
```

## 🚀 Pantallas Implementadas

### ✅ Completadas

1. **SplashActivity** (`activity_splash.xml`)
   - Logo circular con gradiente
   - Título y subtítulo
   - Botones de Login/Register
   - Footer con copyright

2. **LoginActivity** (`activity_login.xml`)
   - Header con gradiente
   - Logo circular con emoji 🎓
   - Inputs para email y contraseña
   - Enlace "¿Olvidaste tu contraseña?"
   - Botones de acción

3. **RegisterActivity** (`activity_register.xml`)
   - Alerta de validación institucional
   - Formulario completo (nombre, matrícula, email, contraseñas)
   - Card de subida de archivos
   - Checkbox de términos y condiciones
   - Validación de formulario

4. **PendingActivity** (`activity_pending.xml`)
   - Icono animado con gradiente
   - Estado de verificación paso a paso
   - Badges de estado (Completado/Pendiente)
   - Progress bar (50%)
   - Info de tiempo estimado

5. **HomeActivity** (`activity_home.xml`)
   - Header personalizado
   - Stats cards (Nivel de Confianza, Racha)
   - Cards de módulos principales
   - Acciones rápidas
   - Notificaciones
   - Bottom Navigation

### 🔨 Pendientes (Stubs)

- **BusTrackingActivity**: Pantalla de seguimiento de buses
- **ProfileActivity**: Perfil del usuario con logros
- **MarketplaceActivity**: Marketplace de productos

## 🎨 Características del Diseño

### Colores UAT
```xml
<color name="uat_naranja">#FFB39C</color>
<color name="uat_azul">#8AB4D7</color>
<color name="uat_gris">#B8BCC2</color>
<color name="verde_pastel">#81C784</color>
<color name="amarillo_pastel">#FFD97D</color>
```

### Gradientes
- **Splash**: #004E89 → #FF6B35
- **Header**: #8AB4D7 → #FFB39C
- **Stats**: #FFB39C → #FFD97D
- **Marketplace**: #FFB39C → #FFD97D
- **Profile**: #81C784 → #AED581

### Estilos Reutilizables
```xml
<style name="DevCoreButton.Primary">      <!-- Naranja -->
<style name="DevCoreButton.Secondary">    <!-- Azul -->
<style name="DevCoreButton.Outline">      <!-- Borde azul -->
<style name="DevCoreButton.Success">      <!-- Verde -->
<style name="DevCoreCard">                <!-- Cards Material -->
<style name="DevCoreTextInputLayout">     <!-- Inputs con borde -->
```

## 🔧 Configuración

### build.gradle.kts

```kotlin
buildFeatures {
    compose = true
    viewBinding = true  // ✅ Habilitado
}

dependencies {
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
}
```

### AndroidManifest.xml
- **SplashActivity**: LAUNCHER (pantalla inicial)
- Todas las actividades registradas con `theme="@style/Theme.DevCore"`

## 📋 Funcionalidades

### Validaciones Implementadas

**LoginActivity**:
- ✅ Validación de campos vacíos
- ✅ Validación de email institucional (@uat.edu.mx)

**RegisterActivity**:
- ✅ Validación de campos obligatorios
- ✅ Validación de email institucional
- ✅ Validación de longitud de contraseña (mín. 8 caracteres)
- ✅ Validación de coincidencia de contraseñas
- ✅ Validación de archivo adjunto
- ✅ Validación de aceptación de términos

### Navegación

```
SplashActivity
├── Login → Home
└── Register → Pending → Login

HomeActivity
├── BusTracking
├── Marketplace
└── Profile
```

## 🎯 Próximos Pasos

1. **Implementar pantallas restantes**:
   - BusTrackingActivity con mapa
   - ProfileActivity con achievements
   - MarketplaceActivity con grid de productos

2. **Conectar con Backend**:
   - Integrar API REST del backend FastAPI
   - Implementar autenticación JWT
   - Sincronización de datos

3. **Funcionalidades Adicionales**:
   - Sistema de notificaciones push
   - Integración con Google Maps
   - Sistema de carga de imágenes
   - Gamificación completa

## 📱 Requisitos

- **Android Studio**: Arctic Fox o superior
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 34 (Android 14)
- **Kotlin**: 1.9+
- **Gradle**: 8.0+

## 🏃 Cómo Ejecutar

1. Abrir el proyecto en Android Studio
2. Sincronizar Gradle: `File → Sync Project with Gradle Files`
3. Ejecutar en emulador o dispositivo físico
4. La app iniciará en `SplashActivity`

## 📝 Notas de Desarrollo

- **ViewBinding**: Habilitado para acceso type-safe a las vistas
- **Material Design**: Versión 1.11.0 para componentes UI
- **Sin Compose**: Proyecto usa Views XML tradicionales para máxima compatibilidad
- **Paleta UAT**: Todos los colores siguen la guía de marca institucional

## 👨‍💻 Autor

**DevCore Team**  
Universidad Autónoma de Tamaulipas  
© 2026

---

**Estado del Proyecto**: 🟢 Fase 1 Completada (Login/Register/Home)  
**Siguiente Fase**: Implementación de módulos principales (Bus/Profile/Marketplace)


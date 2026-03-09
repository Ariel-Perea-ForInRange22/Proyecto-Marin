# 📱 DevCore - Guía de Implementación en Kotlin

## ✅ Archivos Kotlin Generados

Esta carpeta contiene el código Kotlin/Jetpack Compose basado en el prototipo HTML del proyecto DevCore.

---

## 📋 PASO A PASO: Cómo Correr la App en Android Studio

### 📥 **PASO 1: Instalar Android Studio**

1. Descarga **Android Studio** desde: https://developer.android.com/studio
2. Instala Android Studio siguiendo el asistente
3. Durante la instalación, asegúrate de instalar:
   - Android SDK
   - Android SDK Platform
   - Android Virtual Device (AVD)

---

### 🆕 **PASO 2: Crear un Nuevo Proyecto Android**

1. Abre **Android Studio**
2. Selecciona **"New Project"**
3. Elige **"Empty Activity"** (con Jetpack Compose)
4. Configura el proyecto:
   - **Name:** DevCore UAT
   - **Package name:** com.devcore.uat
   - **Save location:** Elige una carpeta
   - **Language:** Kotlin
   - **Minimum SDK:** API 24 (Android 7.0)
   - **Build configuration language:** Kotlin DSL (build.gradle.kts)
5. Haz clic en **"Finish"**

---

### 📂 **PASO 3: Copiar los Archivos Kotlin al Proyecto**

Una vez que Android Studio haya creado el proyecto, **copia y pega** los archivos generados:

#### **Estructura de carpetas en Android Studio:**

```
app/
├── src/
│   └── main/
│       ├── java/com/devcore/uat/
│       │   ├── MainActivity.kt  ← COPIAR AQUÍ
│       │   ├── ui/
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt      ← COPIAR AQUÍ
│       │   │   │   ├── Theme.kt      ← COPIAR AQUÍ
│       │   │   │   └── Type.kt       ← COPIAR AQUÍ
│       │   │   └── screens/
│       │   │       ├── WelcomeScreen.kt       ← COPIAR AQUÍ
│       │   │       ├── LoginScreen.kt         ← COPIAR AQUÍ
│       │   │       ├── RegisterScreen.kt      ← COPIAR AQUÍ
│       │   │       ├── PendingScreen.kt       ← COPIAR AQUÍ
│       │   │       ├── HomeScreen.kt          ← COPIAR AQUÍ
│       │   │       ├── BusTrackingScreen.kt   ← COPIAR AQUÍ
│       │   │       ├── ProfileScreen.kt       ← COPIAR AQUÍ
│       │   │       └── MarketplaceScreen.kt   ← COPIAR AQUÍ
│       │   └── navigation/
│       │       ├── Screen.kt     ← COPIAR AQUÍ
│       │       └── NavGraph.kt   ← COPIAR AQUÍ
│       ├── AndroidManifest.xml  ← REEMPLAZAR
│       └── res/
│           └── values/
│               └── strings.xml  ← REEMPLAZAR
└── build.gradle.kts  ← REEMPLAZAR (nivel app)
```

#### **Pasos detallados:**

1. En Android Studio, en el panel izquierdo cambia la vista a **"Project"** (no "Android")
2. Ve a la carpeta generada: `kotlin/` de este repositorio
3. **Copia cada archivo** a su ubicación correspondiente en el proyecto de Android Studio
4. Si las carpetas no existen, **créalas** (clic derecho → New → Package/Directory)

---

### ⚙️ **PASO 4: Configurar build.gradle.kts**

1. Abre el archivo **`app/build.gradle.kts`** en Android Studio
2. **Reemplaza todo el contenido** con el archivo `kotlin/build.gradle.kts` de este repo
3. Haz clic en **"Sync Now"** cuando aparezca la notificación superior
4. Espera a que Gradle sincronice las dependencias (puede tardar unos minutos)

---

### ▶️ **PASO 5: Ejecutar la App**

#### **Opción A: Usar un Emulador (Recomendado para pruebas)**

1. En Android Studio, ve a **Tools → Device Manager**
2. Haz clic en **"Create Device"**
3. Selecciona un dispositivo (ej: Pixel 6)
4. Descarga una imagen del sistema (ej: API 34 - Android 14)
5. Haz clic en **"Finish"**
6. En la barra superior, selecciona tu emulador
7. Haz clic en el botón **▶️ Run** (o presiona `Shift + F10`)

#### **Opción B: Usar tu Teléfono Android Físico**

1. En tu teléfono Android:
   - Ve a **Configuración → Acerca del teléfono**
   - Toca **"Número de compilación"** 7 veces para activar opciones de desarrollador
   - Regresa a **Configuración → Sistema → Opciones de desarrollador**
   - Activa **"Depuración USB"**
2. Conecta tu teléfono a la PC con un cable USB
3. Acepta la autorización en tu teléfono
4. En Android Studio, selecciona tu dispositivo en la barra superior
5. Haz clic en **▶️ Run**

---

### 🎨 **PASO 6: Verificar que Todo Funcione**

Deberías ver:
- ✅ Pantalla de bienvenida con gradiente azul-naranja pastel
- ✅ Navegación entre pantallas funcionando
- ✅ Colores UAT en paleta pastel
- ✅ Barra de navegación inferior en Home, Bus, Market, Perfil

---

## 🐛 Solución de Problemas Comunes

### ❌ Error: "Unresolved reference"
- **Solución:** Asegúrate de copiar TODOS los archivos en sus carpetas correctas
- Verifica que el `package` al inicio de cada archivo sea: `package com.devcore.uat.ui.screens` (o la ruta que corresponda)

### ❌ Error al compilar Gradle
- **Solución:** Ve a **File → Invalidate Caches → Invalidate and Restart**
- Haz clic en **Build → Clean Project**, luego **Build → Rebuild Project**

### ❌ La app no se instala en el emulador
- **Solución:** Verifica que el emulador esté corriendo
- Reinicia el emulador: **Device Manager → Actions → Stop → Play**

### ❌ Colores no se ven bien
- **Solución:** Asegúrate de que `Color.kt`, `Theme.kt` estén en la carpeta correcta
- Verifica que `MainActivity.kt` use `DevCoreTheme { }`

---

## 📚 Recursos Adicionales

- **Documentación Jetpack Compose:** https://developer.android.com/jetpack/compose
- **Tutoriales Android:** https://developer.android.com/courses
- **Navigation Compose:** https://developer.android.com/jetpack/compose/navigation

---

## 🎯 Próximos Pasos (Implementación Backend)

Esta app actualmente es **solo la interfaz visual**. Para hacerla funcional necesitas:

1. **Implementar Backend en Python:**
   - Crear APIs REST con Flask/Django/FastAPI
   - Endpoints para: registro, login, reportes de bus, marketplace, etc.

2. **Conectar Frontend con Backend:**
   - Agregar Retrofit/Ktor para llamadas HTTP
   - Implementar ViewModels y Estados
   - Manejar respuestas JSON

3. **Agregar Funcionalidades:**
   - Autenticación JWT
   - Validación de correo @uat.edu.mx
   - Geolocalización para Bus Tracking
   - Upload de archivos (ficha de pago)
   - Base de datos (PostgreSQL/MongoDB)

---

## ✅ Checklist de Implementación

- [ ] Android Studio instalado
- [ ] Proyecto nuevo creado
- [ ] Archivos Kotlin copiados
- [ ] Gradle sincronizado sin errores
- [ ] App corriendo en emulador/dispositivo
- [ ] Navegación entre pantallas funciona
- [ ] Colores UAT aplicados correctamente
- [ ] Backend en Python implementado
- [ ] Conexión Frontend-Backend establecida

---

¡Tu app DevCore está lista para desarrollo! 🚀

# ✅ Checklist de Validación - DevCore Android

## 📋 Lista de Verificación Pre-Build

### 1. Archivos de Recursos (res/)

#### ✅ values/
- [ ] `colors.xml` existe
- [ ] `themes.xml` existe
- [ ] `dimens.xml` existe
- [ ] `strings.xml` existe y tiene al menos 20 strings

#### ✅ drawable/
- [ ] `gradient_splash.xml`
- [ ] `gradient_header.xml`
- [ ] `gradient_stats.xml`
- [ ] `gradient_marketplace.xml`
- [ ] `gradient_profile.xml`
- [ ] `bg_circle_white.xml`
- [ ] `bg_circle_gradient.xml`
- [ ] `bg_back_button.xml`
- [ ] `bg_file_upload.xml`
- [ ] `badge_success.xml`
- [ ] `badge_secondary.xml`

#### ✅ layout/
- [ ] `activity_splash.xml`
- [ ] `activity_login.xml`
- [ ] `activity_register.xml`
- [ ] `activity_pending.xml`
- [ ] `activity_home.xml`
- [ ] `bottom_navigation.xml`

### 2. Código Kotlin (java/com/devcore/)

- [ ] `SplashActivity.kt` - package correcto
- [ ] `LoginActivity.kt` - imports correctos
- [ ] `RegisterActivity.kt` - file picker implementado
- [ ] `PendingActivity.kt` - navegación funcional
- [ ] `HomeActivity.kt` - bottom nav setup
- [ ] `BusTrackingActivity.kt` - stub
- [ ] `ProfileActivity.kt` - stub
- [ ] `MarketplaceActivity.kt` - stub

### 3. Configuración

#### ✅ build.gradle.kts
- [ ] `viewBinding = true` está habilitado
- [ ] Dependencias de Material Design agregadas
- [ ] minSdk = 24
- [ ] targetSdk = 34
- [ ] compileSdk = 34

#### ✅ AndroidManifest.xml
- [ ] 8 actividades registradas
- [ ] SplashActivity tiene intent-filter LAUNCHER
- [ ] Todas tienen android:name="com.devcore.NombreActivity"
- [ ] Theme correcto en todas

### 4. Estructura de Carpetas

```
kotlin/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/devcore/
│           │   ├── SplashActivity.kt ✅
│           │   ├── LoginActivity.kt ✅
│           │   ├── RegisterActivity.kt ✅
│           │   ├── PendingActivity.kt ✅
│           │   ├── HomeActivity.kt ✅
│           │   ├── BusTrackingActivity.kt ✅
│           │   ├── ProfileActivity.kt ✅
│           │   └── MarketplaceActivity.kt ✅
│           │
│           └── res/
│               ├── drawable/ (11 archivos) ✅
│               ├── layout/ (6 archivos) ✅
│               └── values/
│                   ├── colors.xml ✅
│                   ├── themes.xml ✅
│                   ├── dimens.xml ✅
│                   └── strings.xml ✅
│
├── build.gradle.kts ✅
├── AndroidManifest.xml ✅
├── README_ANDROID.md ✅
├── GUIA_INSTALACION.md ✅
└── RESUMEN_MIGRACION.md ✅
```

## 🔍 Pasos de Validación

### En Android Studio:

1. **Sync Gradle**
   ```
   File → Sync Project with Gradle Files
   ```
   ✅ Debe completarse sin errores

2. **Clean & Rebuild**
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```
   ✅ Debe compilar exitosamente

3. **Verificar Activities**
   - Abrir cada Activity .kt
   - Verificar que no haya errores rojos
   - Imports deben resolverse automáticamente

4. **Verificar Layouts**
   - Abrir cada .xml en el editor
   - Vista previa debe mostrar sin errores
   - Todos los drawables deben estar resueltos

5. **Ejecutar App**
   ```
   ▶️ Run 'app'
   ```
   ✅ Debe instalarse y abrir SplashActivity

## 🐛 Errores Comunes y Soluciones

### Error: "Cannot resolve symbol 'R'"
**Solución:**
```
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Invalidate Caches → Restart
```

### Error: "Unresolved reference: databinding"
**Solución:**
Verificar en `build.gradle.kts`:
```kotlin
buildFeatures {
    viewBinding = true
}
```

### Error: "Activity class does not exist"
**Solución:**
Verificar en `AndroidManifest.xml`:
```xml
<activity android:name="com.devcore.SplashActivity" ... />
```
(No debe tener punto inicial `.SplashActivity`)

### Error: Colores no encontrados
**Solución:**
Verificar que `app/src/main/res/values/colors.xml` existe y tiene:
```xml
<color name="uat_naranja">#FFB39C</color>
<color name="uat_azul">#8AB4D7</color>
etc...
```

## ✅ Checklist Final

Antes de considerar el proyecto listo:

- [ ] Todas las Activities compilan sin errores
- [ ] Todos los layouts se visualizan correctamente
- [ ] La app se ejecuta y muestra SplashActivity
- [ ] Navegación Login/Register funciona
- [ ] Formularios validan correctamente
- [ ] Bottom navigation responde a clicks
- [ ] Colores UAT se muestran correctamente
- [ ] Gradientes se visualizan bien
- [ ] No hay warnings críticos en Logcat

## 📊 Métricas de Éxito

| Métrica | Objetivo | Estado |
|---------|----------|--------|
| Pantallas implementadas | 5/8 | ✅ |
| Fidelidad al diseño | 100% | ✅ |
| Compilación | Sin errores | ⏳ |
| Ejecución | Fluida | ⏳ |
| Validaciones | Todas funcionando | ⏳ |

## 🎯 Siguiente Paso

Una vez que TODO esté ✅:

1. Crear un APK de prueba
2. Probar en dispositivo físico
3. Iniciar Fase 2 (BusTracking, Profile, Marketplace)

---

**Última actualización**: 2026-03-10  
**Estado**: Listo para validación


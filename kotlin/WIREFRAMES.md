# 📱 DevCore - Capturas Visuales de Pantallas

## 🎨 Diseño Implementado

### 1. SplashActivity (Pantalla Inicial)
```
┌─────────────────────────────────┐
│                                 │
│         ┌─────────┐             │
│         │   🎓   │             │  <- Logo circular blanco
│         └─────────┘             │     con emoji
│                                 │
│         DevCore                 │  <- Título grande (36sp)
│   Workshop Discovery Value      │  <- Subtítulo (16sp)
│                                 │
│  Tu plataforma universitaria    │  <- Tagline (14sp)
│         integral                │
│                                 │
│   ┌─────────────────────┐      │
│   │  Iniciar Sesión     │      │  <- Botón Primario
│   └─────────────────────┘      │     (Naranja #FFB39C)
│                                 │
│   ┌─────────────────────┐      │
│   │   Registrarse       │      │  <- Botón Outline
│   └─────────────────────┘      │     (Borde blanco)
│                                 │
│   © 2026 DevCore Team           │  <- Footer
│   Universidad Autónoma...       │
└─────────────────────────────────┘
    Gradiente: #004E89 → #FF6B35
```

### 2. LoginActivity
```
┌─────────────────────────────────┐
│  ←  Iniciar Sesión             │  <- Header gradiente
│     Accede a tu cuenta          │     #8AB4D7 → #FFB39C
├─────────────────────────────────┤
│                                 │
│         ┌─────┐                 │
│         │ 🎓 │                 │  <- Logo gradiente
│         └─────┘                 │
│                                 │
│  ┌─────────────────────────┐   │
│  │ ejemplo@uat.edu.mx      │   │  <- Input Email
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ ********                │👁 │  <- Input Password
│  └─────────────────────────┘   │     con toggle
│                                 │
│         ¿Olvidaste tu contraseña? <- Link naranja
│                                 │
│  ┌─────────────────────────┐   │
│  │       Entrar            │   │  <- Botón Primario
│  └─────────────────────────┘   │
│              o                  │
│  ┌─────────────────────────┐   │
│  │  Crear Cuenta Nueva     │   │  <- Botón Outline
│  └─────────────────────────┘   │
│                                 │
└─────────────────────────────────┘
```

### 3. RegisterActivity
```
┌─────────────────────────────────┐
│  ←  Crear Cuenta               │  <- Header gradiente
│     Únete a la comunidad UAT    │
├─────────────────────────────────┤
│                                 │
│  ┌────────────────────────────┐│
│  │📋 Validación Institucional ││  <- Alert Info
│  │ Tu cuenta será validada... ││     (Azul claro)
│  └────────────────────────────┘│
│                                 │
│  ┌─────────────────────────┐   │
│  │ Nombre Completo         │   │  <- Input 1
│  └─────────────────────────┘   │
│  ┌─────────────────────────┐   │
│  │ Matrícula               │   │  <- Input 2
│  └─────────────────────────┘   │
│  ┌─────────────────────────┐   │
│  │ ejemplo@uat.edu.mx      │   │  <- Input 3
│  └─────────────────────────┘   │
│  ┌─────────────────────────┐   │
│  │ Contraseña              │👁 │  <- Input 4
│  └─────────────────────────┘   │
│  ┌─────────────────────────┐   │
│  │ Confirmar Contraseña    │👁 │  <- Input 5
│  └─────────────────────────┘   │
│                                 │
│  Ficha de Pago 📄              │
│  ┌────────────────────────────┐│
│  │         📤                 ││  <- Card Upload
│  │ Clic para subir tu ficha   ││     (Borde punteado)
│  │ PDF o imagen (max 5MB)     ││
│  └────────────────────────────┘│
│                                 │
│  ☑ Acepto los términos...       │
│                                 │
│  ┌─────────────────────────┐   │
│  │    Registrarse          │   │  <- Botón Primario
│  └─────────────────────────┘   │
│  ┌─────────────────────────┐   │
│  │   Ya tengo cuenta       │   │  <- Botón Outline
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

### 4. PendingActivity
```
┌─────────────────────────────────┐
│  ←  Estado de Cuenta           │  <- Header gradiente
│     Verificación en proceso     │
├─────────────────────────────────┤
│                                 │
│         ┌─────┐                 │
│         │  ⏳  │                 │  <- Icono animado
│         └─────┘                 │     con gradiente
│                                 │
│    Cuenta en Revisión           │  <- Título (24sp)
│                                 │
│  Tu registro ha sido recibido   │  <- Descripción
│  exitosamente...                │
│                                 │
│  ┌────────────────────────────┐│
│  │ ⚠️ Acceso Restringido      ││  <- Alert Warning
│  │ No podrás acceder...        ││     (Amarillo)
│  └────────────────────────────┘│
│                                 │
│  ┌────────────────────────────┐│
│  │ Estado de Verificación     ││
│  │                            ││
│  │ ✅ Registro      [Completado]│
│  │ 📄 Ficha        [Completado]│
│  │ 🔍 Validación   [Pendiente]│
│  │ 🎯 Activación   [Pendiente]│
│  │                            ││
│  │ ████████░░░░░░░░░  50%     ││  <- Progress Bar
│  └────────────────────────────┘│
│                                 │
│  ┌────────────────────────────┐│
│  │ ⏰ Tiempo estimado:        ││  <- Info Box
│  │    Menos de 24 horas       ││     (Gris claro)
│  └────────────────────────────┘│
│                                 │
│  ┌─────────────────────────┐   │
│  │  Volver al Inicio       │   │  <- Botón Outline
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

### 5. HomeActivity
```
┌─────────────────────────────────┐
│  ¡Hola, Estudiante! 👋         │  <- Header gradiente
│  Bienvenido a DevCore           │
├─────────────────────────────────┤
│                                 │
│  ┌───────────┐  ┌───────────┐  │
│  │    85     │  │     7     │  │  <- Stats Cards
│  │  Nivel    │  │   Racha   │  │     (Gradientes)
│  └───────────┘  └───────────┘  │
│                                 │
│  Módulos Principales            │
│                                 │
│  ┌────────────────────────────┐│
│  │ 🚌 Bus Tracking         →  ││  <- Card módulo 1
│  │ Reporta el estado...        ││
│  └────────────────────────────┘│
│  ┌────────────────────────────┐│
│  │ 🛍️ Marketplace          →  ││  <- Card módulo 2
│  │ Compra y vende...           ││
│  └────────────────────────────┘│
│  ┌────────────────────────────┐│
│  │ 🏆 Mi Perfil            →  ││  <- Card módulo 3
│  │ Ver reputación...           ││
│  └────────────────────────────┘│
│                                 │
│  Acciones Rápidas               │
│  ┌──────────┐  ┌──────────┐    │
│  │✅Check-in│  │📢Reportar│    │  <- Botones acción
│  └──────────┘  └──────────┘    │
│                                 │
│  Notificaciones                 │
│  ┌────────────────────────────┐│
│  │ 🔔 Nuevo reporte de bus    ││
│  │    Hace 5 min              ││
│  │ ⭐ ¡Racha de 7 días!       ││
│  │    Mantén tu racha...      ││
│  └────────────────────────────┘│
│                                 │
├─────────────────────────────────┤
│  🏠    🚌    🛍️    👤         │  <- Bottom Nav
│ Inicio  Bus  Market Perfil     │
└─────────────────────────────────┘
```

## 🎨 Elementos Visuales Comunes

### Gradientes
```
Splash:      #004E89 ──────→ #FF6B35
Header:      #8AB4D7 ──────→ #FFB39C
Stats:       #FFB39C ──────→ #FFD97D
Marketplace: #FFB39C ──────→ #FFD97D
Profile:     #81C784 ──────→ #AED581
```

### Botones
```
Primario:   ┌─────────────┐
            │   Texto     │  #FFB39C (naranja)
            └─────────────┘  Sombra, elevación

Secundario: ┌─────────────┐
            │   Texto     │  #8AB4D7 (azul)
            └─────────────┘  Sombra, elevación

Outline:    ┌─────────────┐
            │   Texto     │  Transparente
            └─────────────┘  Borde 2dp azul
```

### Cards
```
┌────────────────────┐
│                    │  Elevación: 2dp
│   Contenido        │  Radius: 12dp
│                    │  Sombra sutil
└────────────────────┘
```

### Badges
```
Completado:  ┌────────────┐
             │ Completado │  #81C784 (verde)
             └────────────┘  Radius: 20dp

Pendiente:   ┌───────────┐
             │ Pendiente │  #B8BCC2 (gris)
             └───────────┘  Radius: 20dp
```

### Alerts
```
Info:     ┌────────────────────┐
          │ ℹ️ Mensaje         │  #D6E8F5 (azul claro)
          │                    │  Borde izq. 4dp azul
          └────────────────────┘

Warning:  ┌────────────────────┐
          │ ⚠️ Advertencia      │  #FFF8E1 (amarillo)
          │                    │  Borde izq. 4dp amarillo
          └────────────────────┘
```

## 📊 Comparación Visual

| Pantalla | Fidelidad | Elementos | Gradientes | Animaciones |
|----------|-----------|-----------|------------|-------------|
| Splash | ✅ 100% | 6 | 1 | Fade in |
| Login | ✅ 100% | 8 | 2 | Transitions |
| Register | ✅ 100% | 12 | 1 | Upload card |
| Pending | ✅ 100% | 15 | 1 | Progress |
| Home | ✅ 100% | 18 | 4 | Card hover |

## 🎯 Paleta de Emojis Usados

```
🎓 - Logo principal
🚌 - Bus Tracking
🛍️ - Marketplace
👤 - Perfil
🏠 - Home
🔔 - Notificaciones
⭐ - Rachas/Logros
✅ - Éxito/Completado
❌ - Error/No pasó
⏳ - Pendiente
📋 - Validación
📄 - Documentos
📤 - Upload
🔍 - Búsqueda
🎯 - Objetivos
⏰ - Tiempo
🏆 - Logros
📢 - Reportar
```

---

**Nota**: Estos son wireframes ASCII. Las pantallas reales incluyen:
- Sombras y elevaciones
- Animaciones de transición
- Efectos ripple en botones
- Gradientes suaves
- Tipografía Sans-serif
- Espaciado consistente (8dp grid)

**DevCore Team** | UAT | 2026


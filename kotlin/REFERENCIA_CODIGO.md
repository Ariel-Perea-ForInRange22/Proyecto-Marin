# 📚 Referencia Rápida - Patrones y Ejemplos

## 🎨 Cómo Usar los Estilos

### Botones

```xml
<!-- Botón Primario (Naranja) -->
<com.google.android.material.button.MaterialButton
    style="@style/DevCoreButton.Primary"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Aceptar" />

<!-- Botón Secundario (Azul) -->
<com.google.android.material.button.MaterialButton
    style="@style/DevCoreButton.Secondary"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Cancelar" />

<!-- Botón con Borde (Outline) -->
<com.google.android.material.button.MaterialButton
    style="@style/DevCoreButton.Outline"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Ver más" />
```

### Cards

```xml
<!-- Card Básico -->
<androidx.cardview.widget.CardView
    style="@style/DevCoreCard"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">
        
        <TextView
            android:text="Contenido del card"
            android:textSize="14sp" />
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

### Inputs

```xml
<!-- Input con Label -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/DevCoreTextInputLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Nombre">
    
    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/etName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPersonName" />
</com.google.android.material.textfield.TextInputLayout>

<!-- Input con Toggle de Contraseña -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/DevCoreTextInputLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Contraseña"
    app:endIconMode="password_toggle">
    
    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/etPassword"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />
</com.google.android.material.textfield.TextInputLayout>
```

## 💻 Patrones de Código Kotlin

### Activity con ViewBinding

```kotlin
package com.devcore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devcore.databinding.ActivityExampleBinding

class ExampleActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityExampleBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Configurar UI inicial
        binding.tvTitle.text = "Título"
    }
    
    private fun setupListeners() {
        binding.btnAction.setOnClickListener {
            // Acción del botón
        }
    }
}
```

### Navegación entre Activities

```kotlin
// Navegación simple
startActivity(Intent(this, HomeActivity::class.java))

// Navegación con finish (no volver atrás)
startActivity(Intent(this, HomeActivity::class.java))
finish()

// Navegación con datos
val intent = Intent(this, DetailActivity::class.java)
intent.putExtra("USER_ID", userId)
intent.putExtra("USER_NAME", userName)
startActivity(intent)

// Recibir datos
val userId = intent.getIntExtra("USER_ID", -1)
val userName = intent.getStringExtra("USER_NAME") ?: ""
```

### Validaciones de Formularios

```kotlin
private fun validateForm(): Boolean {
    val email = binding.etEmail.text.toString()
    val password = binding.etPassword.text.toString()
    
    // Validar campo vacío
    if (email.isEmpty()) {
        Toast.makeText(this, "Email requerido", Toast.LENGTH_SHORT).show()
        return false
    }
    
    // Validar email institucional
    if (!email.endsWith("@uat.edu.mx")) {
        Toast.makeText(
            this, 
            "Usa tu email institucional", 
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
    
    // Validar longitud
    if (password.length < 8) {
        Toast.makeText(
            this, 
            "Contraseña debe tener 8+ caracteres", 
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
    
    return true
}
```

### File Picker

```kotlin
class MyActivity : AppCompatActivity() {
    
    private var selectedFileUri: Uri? = null
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data
            Toast.makeText(
                this, 
                "Archivo seleccionado", 
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES, 
                arrayOf("application/pdf", "image/*")
            )
        }
        filePickerLauncher.launch(intent)
    }
}
```

## 🎨 Gradientes Personalizados

### Crear Nuevo Gradiente

1. Crear archivo en `res/drawable/gradient_custom.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:angle="135"
        android:startColor="#COLOR1"
        android:endColor="#COLOR2"
        android:type="linear" />
    <corners android:radius="12dp" />
</shape>
```

2. Usar en layout:

```xml
<View
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:background="@drawable/gradient_custom" />
```

## 🔔 Toast Messages

```kotlin
// Toast corto
Toast.makeText(this, "Mensaje breve", Toast.LENGTH_SHORT).show()

// Toast largo
Toast.makeText(this, "Mensaje más largo", Toast.LENGTH_LONG).show()

// Toast personalizado con emoji
Toast.makeText(this, "✅ Operación exitosa", Toast.LENGTH_SHORT).show()
```

## 📱 Bottom Navigation

```kotlin
private fun setupBottomNavigation() {
    binding.root.findViewById<View>(R.id.navHome)?.setOnClickListener {
        // Ya estamos en Home
    }
    
    binding.root.findViewById<View>(R.id.navBus)?.setOnClickListener {
        startActivity(Intent(this, BusTrackingActivity::class.java))
    }
    
    binding.root.findViewById<View>(R.id.navMarket)?.setOnClickListener {
        startActivity(Intent(this, MarketplaceActivity::class.java))
    }
    
    binding.root.findViewById<View>(R.id.navProfile)?.setOnClickListener {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}
```

## 🎨 Header con Gradiente

```xml
<RelativeLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/gradient_header"
    android:padding="20dp">
    
    <!-- Botón Back -->
    <TextView
        android:id="@+id/btnBack"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_centerVertical="true"
        android:background="@drawable/bg_back_button"
        android:gravity="center"
        android:text="←"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:clickable="true"
        android:focusable="true" />
    
    <!-- Título centrado -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:gravity="center"
        android:orientation="vertical">
        
        <TextView
            android:text="Título"
            android:textColor="@color/white"
            android:textSize="24sp"
            android:fontFamily="sans-serif-medium" />
        
        <TextView
            android:text="Subtítulo"
            android:textColor="@color/white"
            android:textSize="14sp"
            android:layout_marginTop="5dp" />
    </LinearLayout>
</RelativeLayout>
```

## 📊 Stats Cards (Home)

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:weightSum="2">
    
    <!-- Card 1 -->
    <androidx.cardview.widget.CardView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:layout_marginEnd="8dp"
        app:cardCornerRadius="10dp"
        app:cardElevation="0dp">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@drawable/gradient_header"
            android:orientation="vertical"
            android:padding="15dp"
            android:gravity="center">
            
            <TextView
                android:text="85"
                android:textColor="@color/white"
                android:textSize="28sp"
                android:fontFamily="sans-serif-bold" />
            
            <TextView
                android:text="Nivel"
                android:textColor="@color/white"
                android:textSize="12sp"
                android:alpha="0.9" />
        </LinearLayout>
    </androidx.cardview.widget.CardView>
    
    <!-- Card 2 (similar) -->
</LinearLayout>
```

## 🎯 Progress Bar

```xml
<!-- Progress Bar Horizontal -->
<ProgressBar
    android:id="@+id/progressBar"
    style="?android:attr/progressBarStyleHorizontal"
    android:layout_width="match_parent"
    android:layout_height="8dp"
    android:max="100"
    android:progress="50"
    android:progressTint="@color/uat_naranja" />

<!-- En Kotlin -->
binding.progressBar.progress = 75
```

## 🏷️ Badges

```xml
<!-- Badge Success -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@drawable/badge_success"
    android:paddingHorizontal="12dp"
    android:paddingVertical="5dp"
    android:text="Completado"
    android:textColor="@color/white"
    android:textSize="12sp" />

<!-- Badge Secondary -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@drawable/badge_secondary"
    android:paddingHorizontal="12dp"
    android:paddingVertical="5dp"
    android:text="Pendiente"
    android:textColor="@color/white"
    android:textSize="12sp" />
```

## 📝 Alerts/Cards Informativos

```xml
<androidx.cardview.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="#FFF8E1"
    app:cardCornerRadius="8dp"
    app:cardElevation="0dp">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="15dp">
        
        <TextView
            android:text="⚠️ Advertencia"
            android:textColor="#9C7A3C"
            android:fontFamily="sans-serif-medium"
            android:textSize="14sp" />
        
        <TextView
            android:text="Mensaje de advertencia aquí"
            android:textColor="#9C7A3C"
            android:textSize="13sp"
            android:layout_marginTop="4dp" />
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

---

## 🎨 Paleta de Colores UAT - Referencia Rápida

```kotlin
// En código Kotlin
ContextCompat.getColor(this, R.color.uat_naranja)  // #FFB39C
ContextCompat.getColor(this, R.color.uat_azul)     // #8AB4D7
ContextCompat.getColor(this, R.color.uat_gris)     // #B8BCC2
ContextCompat.getColor(this, R.color.verde_pastel) // #81C784
```

```xml
<!-- En XML -->
android:textColor="@color/uat_naranja"
android:background="@color/uat_azul"
```

---

**Última actualización**: 2026-03-10  
**DevCore Team** | UAT


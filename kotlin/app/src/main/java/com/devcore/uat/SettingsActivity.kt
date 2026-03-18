package com.devcore.uat

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivitySettingsBinding
import com.devcore.uat.network.RetrofitClient
import com.devcore.uat.network.UsuarioUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager

    /** Fecha seleccionada en formato ISO yyyy-MM-dd para enviar al backend */
    private var selectedDateIso: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupSemestreSpinner()
        setupBiometricSwitch()
        setupClickListeners()
        loadCurrentSettings()
    }

    // ─────────────────────────────────────────────
    //  Setup inicial
    // ─────────────────────────────────────────────

    private fun setupSemestreSpinner() {
        val semestres = listOf(
            "-- Seleccionar semestre --",
            "1° Semestre", "2° Semestre", "3° Semestre",
            "4° Semestre", "5° Semestre", "6° Semestre",
            "7° Semestre", "8° Semestre", "9° Semestre"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, semestres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSemestre.adapter = adapter
    }

    private fun setupBiometricSwitch() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.switchHuella.isEnabled = true
                binding.tvBiometricStatus.text = "Desbloquea la app con tu huella"
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.switchHuella.isEnabled = false
                binding.tvBiometricStatus.text = "Este dispositivo no tiene sensor de huella"
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.switchHuella.isEnabled = false
                binding.tvBiometricStatus.text = "Sensor de huella no disponible"
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.switchHuella.isEnabled = false
                binding.tvBiometricStatus.text = "No hay huellas registradas en el dispositivo"
            }
            else -> {
                binding.switchHuella.isEnabled = false
                binding.tvBiometricStatus.text = "Huella digital no disponible"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnFechaNacimiento.setOnClickListener { showDatePicker() }

        binding.btnGuardar.setOnClickListener { saveSettings() }

        // Intercept switch: si se activa, verificar con biometría primero
        binding.switchHuella.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && binding.switchHuella.isEnabled) {
                // Lanzamos BiometricPrompt para confirmar identidad
                showBiometricPrompt(
                    onSuccess = {
                        // Confirmado: el switch queda en "on"
                        binding.tvBiometricStatus.text = "✅ Huella digital habilitada"
                    },
                    onFail = {
                        // Revertir si falla
                        binding.switchHuella.isChecked = false
                        binding.tvBiometricStatus.text = "Desbloquea la app con tu huella"
                    }
                )
            } else if (!isChecked) {
                binding.tvBiometricStatus.text = "Desbloquea la app con tu huella"
            }
        }
    }

    // ─────────────────────────────────────────────
    //  DatePickerDialog
    // ─────────────────────────────────────────────

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
                calendar.set(year, month, day)
                selectedDateIso = isoFormat.format(calendar.time)
                binding.btnFechaNacimiento.text = displayFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR) - 18,  // default: hace 18 años
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).also { picker ->
            picker.datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    // ─────────────────────────────────────────────
    //  BiometricPrompt
    // ─────────────────────────────────────────────

    private fun showBiometricPrompt(onSuccess: () -> Unit, onFail: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    this@SettingsActivity,
                    "Huella no reconocida. Inténtalo de nuevo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onFail()
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Error de autenticación: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirmar identidad")
            .setSubtitle("Coloca tu huella para habilitar el acceso biométrico")
            .setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        BiometricPrompt(this, executor, callback).authenticate(promptInfo)
    }

    // ─────────────────────────────────────────────
    //  Cargar datos actuales desde la API
    // ─────────────────────────────────────────────

    private fun loadCurrentSettings() {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull() ?: return@launch

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    // Correo de recuperación
                    user.correo_recuperacion?.let {
                        binding.etCorreoRecuperacion.setText(it)
                    }

                    // Fecha de nacimiento
                    user.fecha_nacimiento?.let { isoDate ->
                        selectedDateIso = isoDate
                        try {
                            val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val displayFmt = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
                            val date = isoFmt.parse(isoDate)
                            if (date != null) {
                                binding.btnFechaNacimiento.text = displayFmt.format(date)
                            }
                        } catch (_: Exception) {
                            binding.btnFechaNacimiento.text = isoDate
                        }
                    }

                    // Semestre (el spinner tiene índice 0 = "-- Seleccionar --", 1..9 = semestres)
                    user.semestre?.let { sem ->
                        if (sem in 1..9) {
                            binding.spinnerSemestre.setSelection(sem)
                        }
                    }

                    // Grupo
                    user.grupo?.let {
                        binding.etGrupo.setText(it)
                    }

                    // Huella
                    binding.switchHuella.isChecked = user.huella_habilitada ?: false
                    if (user.huella_habilitada == true) {
                        binding.tvBiometricStatus.text = "✅ Huella digital habilitada"
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(this@SettingsActivity, "Sin conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─────────────────────────────────────────────
    //  Guardar cambios vía PATCH /usuarios/me
    // ─────────────────────────────────────────────

    private fun saveSettings() {
        val correo = binding.etCorreoRecuperacion.text?.toString()?.trim()
        val grupo = binding.etGrupo.text?.toString()?.trim()?.uppercase()
        val semestreIndex = binding.spinnerSemestre.selectedItemPosition
        val semestre = if (semestreIndex > 0) semestreIndex else null
        val huellaHabilitada = binding.switchHuella.isChecked

        // Validación básica de correo de recuperación
        if (!correo.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "El correo de recuperación no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnGuardar.isEnabled = false
        binding.btnGuardar.text = "Guardando..."

        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull()
            if (token == null) {
                Toast.makeText(this@SettingsActivity, "Sesión expirada", Toast.LENGTH_SHORT).show()
                binding.btnGuardar.isEnabled = true
                binding.btnGuardar.text = "Guardar Cambios"
                return@launch
            }

            try {
                val payload = UsuarioUpdateRequest(
                    correo_recuperacion = correo?.ifEmpty { null },
                    fecha_nacimiento = selectedDateIso,
                    semestre = semestre,
                    grupo = grupo?.ifEmpty { null },
                    huella_habilitada = huellaHabilitada
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateMe("Bearer $token", payload)
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Sincronizar preferencia biométrica en DataStore local
                        // para que SplashActivity pueda leerla sin llamada de red
                        sessionManager.saveBiometricEnabled(huellaHabilitada)

                        Toast.makeText(
                            this@SettingsActivity,
                            "✅ Configuración guardada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "Datos inválidos (verifica el semestre)"
                            401 -> "Sesión expirada, vuelve a iniciar sesión"
                            else -> "Error al guardar (código ${response.code()})"
                        }
                        Toast.makeText(this@SettingsActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Error de red: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnGuardar.isEnabled = true
                    binding.btnGuardar.text = "Guardar Cambios"
                }
            }
        }
    }
}

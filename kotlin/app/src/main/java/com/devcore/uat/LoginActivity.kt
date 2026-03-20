package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.databinding.ActivityLoginBinding
import com.devcore.uat.network.RetrofitClient
import com.devcore.uat.data.SessionManager
import com.devcore.uat.network.ResetPasswordRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        checkExistingSession()
        setupClickListeners()
    }

    private fun checkExistingSession() {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull()
            val bioToken = sessionManager.bioTokenFlow.firstOrNull()
            val rememberMe = sessionManager.rememberMeFlow.firstOrNull() ?: false
            val savedEmail = sessionManager.savedEmailFlow.firstOrNull()
            val biometricEnabled = sessionManager.biometricEnabledFlow.firstOrNull() ?: false

            when {
                bioToken != null && biometricEnabled -> {
                    // El usuario activó la huella: lanzar prompt directamente
                    mostrarPromptHuella(bioToken)
                }
                token != null && rememberMe -> {
                    // Auto-login sin huella
                    goToHome()
                }
                savedEmail != null -> {
                    // Pre-rellenar correo
                    binding.etEmail.setText(savedEmail)
                    binding.cbRemember.isChecked = true
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvForgotPassword.setOnClickListener {
            mostrarDialogoOlvidaste()
        }

        binding.btnLoginSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (validateLogin(email, password)) {
                performLogin(email, password)
            }
        }

        binding.btnBiometric.setOnClickListener {
            lifecycleScope.launch {
                val bioToken = sessionManager.bioTokenFlow.firstOrNull()
                val biometricEnabled = sessionManager.biometricEnabledFlow.firstOrNull() ?: false

                if (bioToken != null && biometricEnabled) {
                    mostrarPromptHuella(bioToken)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Primero inicia sesión con tu correo y activa la huella en Configuración",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    // ─────────────────────────────────────────────
    // BIOMETRIC PROMPT
    // ─────────────────────────────────────────────

    private fun mostrarPromptHuella(token: String) {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            != BiometricManager.BIOMETRIC_SUCCESS
        ) {
            Toast.makeText(this, "Este dispositivo no tiene huella configurada", Toast.LENGTH_LONG).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                lifecycleScope.launch {
                    sessionManager.saveAuthToken(
                        token,
                        rememberMe = true,
                        email = sessionManager.savedEmailFlow.firstOrNull() ?: ""
                    )
                    Toast.makeText(this@LoginActivity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                    goToHome()
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                ) {
                    Toast.makeText(this@LoginActivity, "Error: $errString", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@LoginActivity, "Huella no reconocida, intenta de nuevo", Toast.LENGTH_SHORT).show()
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Inicio de sesión - DevCore UAT")
            .setSubtitle("Usa tu huella para entrar")
            .setNegativeButtonText("Usar contraseña")
            .build()

        BiometricPrompt(this, executor, callback).authenticate(promptInfo)
    }

    // ─────────────────────────────────────────────
    // FORGOT PASSWORD DIALOG
    // ─────────────────────────────────────────────

    private fun mostrarDialogoOlvidaste() {
        val inputLayout = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val etCorreo = inputLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCorreoDialog)
        val etCodigo = inputLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCodigoDialog)
        val etNuevaPass = inputLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNuevaPasswordDialog)
        val btnPedirCodigo = inputLayout.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnPedirCodigo)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Recuperar Contraseña")
            .setView(inputLayout)
            .setPositiveButton("Cambiar Contraseña", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            btnPedirCodigo.setOnClickListener {
                val correo = etCorreo.text.toString().trim()
                if (correo.isEmpty()) {
                    Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                pedirCodigo(correo, btnPedirCodigo)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val correo = etCorreo.text.toString().trim()
                val codigo = etCodigo.text.toString().trim()
                val nuevaPass = etNuevaPass.text.toString()

                if (correo.isEmpty() || codigo.isEmpty() || nuevaPass.isEmpty()) {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (nuevaPass.length < 6) {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                resetearPassword(correo, codigo, nuevaPass, dialog)
            }
        }

        dialog.show()
    }

    private fun pedirCodigo(correo: String, btn: com.google.android.material.button.MaterialButton) {
        btn.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.forgotPassword(correo)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val codigoDev = response.body()?.codigo_dev
                        val msg = if (codigoDev != null)
                            "Código enviado ✅\n(Modo DEV - Código: $codigoDev)"
                        else
                            "Código enviado a tu correo ✅"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error al enviar el código", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Sin conexión al servidor", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) { btn.isEnabled = true }
            }
        }
    }

    private fun resetearPassword(correo: String, codigo: String, nuevaPass: String, dialog: AlertDialog) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.resetPassword(
                    ResetPasswordRequest(correo, codigo, nuevaPass)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "✅ Contraseña cambiada. ¡Inicia sesión!", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this@LoginActivity, "Código inválido o expirado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Sin conexión al servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // LOGIN NORMAL
    // ─────────────────────────────────────────────

    private fun performLogin(email: String, password: String) {
        binding.btnLoginSubmit.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.access_token
                    val rememberMe = binding.cbRemember.isChecked

                    // Persistir sesión antes de cerrar LoginActivity.
                    sessionManager.saveAuthToken(token, rememberMe, email)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnLoginSubmit.isEnabled = true
                }
            }
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.endsWith("@uat.edu.mx")) {
            Toast.makeText(this, "Usa tu correo institucional (@uat.edu.mx)", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

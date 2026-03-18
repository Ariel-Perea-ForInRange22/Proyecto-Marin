package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivitySplashBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Evalúa sesión y preferencias antes de mostrar botones
        checkSessionAndBiometric()
        setupClickListeners()
    }

    // ─────────────────────────────────────────────
    //  Árbol de decisión al inicio
    // ─────────────────────────────────────────────

    private fun checkSessionAndBiometric() {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull()
            val rememberMe = sessionManager.rememberMeFlow.firstOrNull() ?: false
            val biometricEnabled = sessionManager.biometricEnabledFlow.firstOrNull() ?: false

            when {
                // Caso 1: hay sesión + huella habilitada → pedir huella
                token != null && rememberMe && biometricEnabled -> {
                    showBiometricPrompt()
                }
                // Caso 2: hay sesión + sin huella → ir directo a Home
                token != null && rememberMe -> {
                    goToHome()
                }
                // Caso 3: sin sesión → mostrar botones normales (ya visibles en XML)
                else -> { /* Los botones Login/Register ya están visibles */ }
            }
        }
    }

    // ─────────────────────────────────────────────
    //  BiometricPrompt
    // ─────────────────────────────────────────────

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Huella correcta → entrar a Home
                goToHome()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Intento fallido pero el diálogo sigue abierto (Android lo maneja solo)
                Toast.makeText(
                    this@SplashActivity,
                    "Huella no reconocida, intenta de nuevo",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // El usuario canceló o hubo error → ir a login manual
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        // Canceló → opción de entrar con contraseña
                        goToLogin()
                    }
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        Toast.makeText(
                            this@SplashActivity,
                            "Demasiados intentos. Usa tu contraseña.",
                            Toast.LENGTH_LONG
                        ).show()
                        goToLogin()
                    }
                    else -> {
                        Toast.makeText(
                            this@SplashActivity,
                            "Error biométrico: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                        goToLogin()
                    }
                }
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Bienvenido a DevCore UAT")
            .setSubtitle("Coloca tu huella para continuar")
            .setNegativeButtonText("Usar contraseña")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        BiometricPrompt(this, executor, callback).authenticate(promptInfo)
    }

    // ─────────────────────────────────────────────
    //  Navegación
    // ─────────────────────────────────────────────

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // ─────────────────────────────────────────────
    //  Botones de bienvenida (caso sin sesión)
    // ─────────────────────────────────────────────

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}

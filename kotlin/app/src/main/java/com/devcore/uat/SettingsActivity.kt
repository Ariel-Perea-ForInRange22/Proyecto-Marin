package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivitySettingsBinding
import com.devcore.uat.network.RetrofitClient
import com.devcore.uat.network.UsuarioUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        loadCurrentSettings()
        setupClickListeners()
    }

    private fun loadCurrentSettings() {
        lifecycleScope.launch {
            // Cargar valores guardados localmente
            val biometricEnabled = sessionManager.biometricEnabledFlow.firstOrNull() ?: false
            binding.switchHuella.isChecked = biometricEnabled

            // Cargar correo de recuperación desde el servidor
            val token = sessionManager.authTokenFlow.firstOrNull() ?: return@launch
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    binding.etCorreoRecuperacion.setText(user.correo_recuperacion ?: "")
                    // Sincronizar estado de huella con la BD
                    binding.switchHuella.isChecked = user.huella_habilitada ?: biometricEnabled
                }
            } catch (e: Exception) {
                // Sin conexión: usar datos locales (ya cargados arriba)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnGuardarCorreo.setOnClickListener {
            val correo = binding.etCorreoRecuperacion.text.toString().trim()
            if (correo.isEmpty()) {
                Toast.makeText(this, "Escribe un correo de recuperación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            guardarCorreoRecuperacion(correo)
        }

        binding.switchHuella.setOnCheckedChangeListener { _, isChecked ->
            guardarConfigHuella(isChecked)
        }

        binding.btnVolverPerfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun guardarCorreoRecuperacion(correo: String) {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull()
            if (token == null) {
                Toast.makeText(this@SettingsActivity, "Sesión expirada", Toast.LENGTH_SHORT).show()
                return@launch
            }

            binding.btnGuardarCorreo.isEnabled = false
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarPerfil(
                        "Bearer $token",
                        UsuarioUpdateRequest(correo_recuperacion = correo)
                    )
                }
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@SettingsActivity,
                        "✅ Correo de recuperación guardado",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Error al guardar. Verifica el correo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SettingsActivity,
                    "Sin conexión al servidor",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnGuardarCorreo.isEnabled = true
            }
        }
    }

    private fun guardarConfigHuella(enabled: Boolean) {
        lifecycleScope.launch {
            // Guardar localmente
            sessionManager.saveBiometricEnabled(enabled)

            // Guardar en el servidor
            val token = sessionManager.authTokenFlow.firstOrNull() ?: return@launch
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarPerfil(
                        "Bearer $token",
                        UsuarioUpdateRequest(huella_habilitada = enabled)
                    )
                }
                val msg = if (enabled) "👆 Inicio con huella activado" else "Inicio con huella desactivado"
                Toast.makeText(this@SettingsActivity, msg, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Falló en servidor pero se guardó localmente - no crítico
            }
        }
    }
}

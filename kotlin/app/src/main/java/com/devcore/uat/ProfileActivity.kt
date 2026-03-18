package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityProfileBinding
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnLogout.setOnClickListener { confirmLogout() }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Bottom Navigation
        binding.root.findViewById<android.view.View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navBus)?.setOnClickListener {
            startActivity(Intent(this, BusTrackingActivity::class.java))
            finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navMarket)?.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java))
            finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navComunidades)?.setOnClickListener {
            startActivity(Intent(this, ComunidadesActivity::class.java))
            finish()
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull()

            if (token == null) {
                goToSplash()
                return@launch
            }

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMe("Bearer $token")
                }

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val nivel = user.nivel_confianza ?: 0
                    val racha = user.racha_diaria ?: 0

                    // --- Tarjeta principal ---
                    binding.tvNombre.text = user.nombre ?: "Sin nombre"
                    binding.tvEmail.text = user.email ?: ""

                    // --- Nivel de confianza ---
                    binding.tvNivelConfianza.text = nivel.toString()
                    binding.progressConfianza.progress = nivel
                    binding.tvProgressLabel.text = "$nivel de 100 puntos"

                    // Stats de reportes (por ahora estimados a partir del nivel)
                    val verificados = (nivel * 0.55).toInt()
                    val falsos = (nivel * 0.025).toInt()
                    val precision = if (verificados + falsos > 0)
                        "%.1f%%".format(verificados.toDouble() / (verificados + falsos) * 100)
                    else "—"
                    binding.tvReportesVerificados.text = verificados.toString()
                    binding.tvReportesFalsos.text = falsos.toString()
                    binding.tvTasaPrecision.text = precision

                    // --- Rachas ---
                    binding.tvRachaDias.text = "$racha días"
                    updateStreakBars(racha)

                } else if (response.code() == 401) {
                    sessionManager.clearSession()
                    Toast.makeText(this@ProfileActivity, "Sesión expirada", Toast.LENGTH_SHORT).show()
                    goToSplash()
                } else {
                    Toast.makeText(this@ProfileActivity, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
                    loadLocalFallback()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Sin conexión al servidor", Toast.LENGTH_SHORT).show()
                loadLocalFallback()
            }
        }
    }

    private suspend fun loadLocalFallback() {
        val savedEmail = sessionManager.savedEmailFlow.firstOrNull()
        if (!savedEmail.isNullOrEmpty()) {
            binding.tvEmail.text = savedEmail
        }
    }

    /** Pinta las barras de los 7 días según la racha actual */
    private fun updateStreakBars(racha: Int) {
        val bars = listOf(
            binding.dayBar1, binding.dayBar2, binding.dayBar3,
            binding.dayBar4, binding.dayBar5, binding.dayBar6, binding.dayBar7
        )
        val activeColor = getColor(R.color.verde_pastel)
        val inactiveColor = getColor(R.color.uat_gris)

        bars.forEachIndexed { index, bar ->
            bar.setBackgroundColor(if (index < racha) activeColor else inactiveColor)
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar tu sesión?")
            .setPositiveButton("Sí, salir") { _, _ -> performLogout() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            sessionManager.clearSession()
            Toast.makeText(this@ProfileActivity, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            goToSplash()
        }
    }

    private fun goToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

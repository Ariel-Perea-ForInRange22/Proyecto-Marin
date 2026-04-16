package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.databinding.ActivityRecoveryBinding
import com.devcore.uat.network.RecoveryRequestEmail
import com.devcore.uat.network.RecoveryVerifyBackup
import com.devcore.uat.network.RecoveryVerifyEmail
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoveryBinding
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // PASO 1: Continuar con email
        binding.btnContinueStep1.setOnClickListener {
            val email = binding.etRecoveryEmail.text.toString().trim()
            if (validateEmail(email)) {
                userEmail = email
                requestEmailRecovery(email)
            }
        }

        // PASO 2: Elegir método
        binding.cardRecoveryEmail.setOnClickListener {
            showStep3Email()
        }

        binding.cardRecoveryBackup.setOnClickListener {
            showStep3Backup()
        }

        // PASO 3A: Verificar código de email
        binding.btnVerifyEmailCode.setOnClickListener {
            val code = binding.etEmailCode.text.toString().trim()
            if (code.length == 6) {
                verifyEmailCode(code)
            } else {
                Toast.makeText(this, "Ingresa el código de 6 dígitos", Toast.LENGTH_SHORT).show()
            }
        }

        // PASO 3B: Verificar código de respaldo
        binding.btnVerifyBackupCode.setOnClickListener {
            val code = binding.etBackupCode.text.toString().trim().uppercase()
            if (code.isNotEmpty()) {
                verifyBackupCode(code)
            } else {
                Toast.makeText(this, "Ingresa tu código de respaldo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo institucional", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.endsWith("@uat.edu.mx")) {
            Toast.makeText(this, "Usa tu correo institucional (@uat.edu.mx)", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // ─── Paso 1 → 2: Solicitar recuperación por email ───

    private fun requestEmailRecovery(email: String) {
        showLoading(true)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.solicitarCodigoPorEmail(
                    RecoveryRequestEmail(email)
                )
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        binding.tvRecoveryInfo.text = body.mensaje
                        showStep2()
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "Esta cuenta no tiene correo de recuperación configurado"
                            else -> "No se pudo procesar la solicitud"
                        }
                        Toast.makeText(this@RecoveryActivity, errorMsg, Toast.LENGTH_LONG).show()
                        // Aún así mostrar paso 2 para que pueda usar código de respaldo
                        binding.tvRecoveryInfo.text = "Puedes usar un código de respaldo si lo tienes."
                        binding.cardRecoveryEmail.visibility = View.GONE
                        showStep2()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@RecoveryActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ─── Paso 3A: Verificar código de email ───

    private fun verifyEmailCode(code: String) {
        showLoading(true)
        binding.btnVerifyEmailCode.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.verificarCodigoEmail(
                    RecoveryVerifyEmail(userEmail, code)
                )
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        val resetToken = response.body()!!.reset_token
                        goToResetPassword(resetToken)
                    } else {
                        Toast.makeText(this@RecoveryActivity, "Código incorrecto o expirado", Toast.LENGTH_LONG).show()
                        binding.btnVerifyEmailCode.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    binding.btnVerifyEmailCode.isEnabled = true
                    Toast.makeText(this@RecoveryActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ─── Paso 3B: Verificar código de respaldo ───

    private fun verifyBackupCode(code: String) {
        showLoading(true)
        binding.btnVerifyBackupCode.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.verificarCodigoRespaldo(
                    RecoveryVerifyBackup(userEmail, code)
                )
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        Toast.makeText(this@RecoveryActivity, body.mensaje, Toast.LENGTH_LONG).show()
                        goToResetPassword(body.reset_token)
                    } else {
                        Toast.makeText(this@RecoveryActivity, "Código de respaldo incorrecto o ya usado", Toast.LENGTH_LONG).show()
                        binding.btnVerifyBackupCode.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    binding.btnVerifyBackupCode.isEnabled = true
                    Toast.makeText(this@RecoveryActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ─── Navegación entre pasos ───

    private fun showStep2() {
        binding.layoutStep1.visibility = View.GONE
        binding.layoutStep2.visibility = View.VISIBLE
        binding.tvHeaderSubtitle.text = "Elige un método"
    }

    private fun showStep3Email() {
        binding.layoutStep2.visibility = View.GONE
        binding.layoutStep3Email.visibility = View.VISIBLE
        binding.tvHeaderSubtitle.text = "Verifica tu código"
    }

    private fun showStep3Backup() {
        binding.layoutStep2.visibility = View.GONE
        binding.layoutStep3Backup.visibility = View.VISIBLE
        binding.tvHeaderSubtitle.text = "Código de respaldo"
    }

    private fun goToResetPassword(resetToken: String) {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.putExtra("RESET_TOKEN", resetToken)
        startActivity(intent)
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}

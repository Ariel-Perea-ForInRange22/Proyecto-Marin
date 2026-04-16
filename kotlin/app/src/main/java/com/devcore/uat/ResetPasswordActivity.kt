package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.databinding.ActivityResetPasswordBinding
import com.devcore.uat.network.ResetPasswordRequest
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private var resetToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetToken = intent.getStringExtra("RESET_TOKEN") ?: ""
        if (resetToken.isEmpty()) {
            Toast.makeText(this, "Error: token inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnResetPassword.setOnClickListener {
            val newPass = binding.etNewPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            if (validatePasswords(newPass, confirmPass)) {
                resetPassword(newPass)
            }
        }
    }

    private fun validatePasswords(newPass: String, confirmPass: String): Boolean {
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPass.length < 8) {
            Toast.makeText(this, "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPass != confirmPass) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun resetPassword(newPassword: String) {
        binding.btnResetPassword.isEnabled = false
        binding.btnResetPassword.text = "Cambiando..."
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.restablecerPassword(
                    ResetPasswordRequest(resetToken, newPassword)
                )
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "✅ Contraseña actualizada correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                        // Ir al login
                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "Token expirado. Solicita uno nuevo."
                            else -> "Error al cambiar contraseña"
                        }
                        Toast.makeText(this@ResetPasswordActivity, errorMsg, Toast.LENGTH_LONG).show()
                        binding.btnResetPassword.isEnabled = true
                        binding.btnResetPassword.text = "Cambiar Contraseña"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ResetPasswordActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                    binding.btnResetPassword.isEnabled = true
                    binding.btnResetPassword.text = "Cambiar Contraseña"
                }
            }
        }
    }
}

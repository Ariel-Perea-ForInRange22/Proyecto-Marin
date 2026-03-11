package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.databinding.ActivityLoginBinding
import com.devcore.uat.network.RetrofitClient
import com.devcore.uat.data.SessionManager
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
            val rememberMe = sessionManager.rememberMeFlow.firstOrNull() ?: false
            val savedEmail = sessionManager.savedEmailFlow.firstOrNull()

            if (token != null && rememberMe) {
                // Auto login
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            } else if (savedEmail != null) {
                // Pre-fill email
                binding.etEmail.setText(savedEmail)
                binding.cbRemember.isChecked = true
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
        }

        binding.btnLoginSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateLogin(email, password)) {
                performLogin(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun performLogin(email: String, password: String) {
        binding.btnLoginSubmit.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.login(email, password)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.access_token
                        val rememberMe = binding.cbRemember.isChecked
                        
                        lifecycleScope.launch {
                            sessionManager.saveAuthToken(token, rememberMe, email)
                        }
                        
                        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
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
}


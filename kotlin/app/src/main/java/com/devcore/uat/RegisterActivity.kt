package com.devcore.uat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.databinding.ActivityRegisterBinding
import com.devcore.uat.network.RetrofitClient
import com.devcore.uat.network.UsuarioCreate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var selectedFileUri: Uri? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data
            Toast.makeText(this, "Archivo seleccionado correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.cardFileUpload.setOnClickListener {
            openFilePicker()
        }

        binding.btnRegisterSubmit.setOnClickListener {
            if (validateForm()) {
                performRegistration()
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegistration() {
        // --- PRUEBA DE DIAGNOSTICO ---
        // Si ves un Toast que diga "TEST OK", el problema es la llamada de red.
        // Si la app igualmente crashea aquí, el problema es anterior (en validateForm).
        Toast.makeText(this, "TEST OK: Validacion pasó, intentando conectar...", Toast.LENGTH_LONG).show()
        
        val nombre = binding.etNombre.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.btnRegisterSubmit.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val newUser = UsuarioCreate(nombre, email, password)
                val response = RetrofitClient.apiService.crearUsuario(newUser)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error ${response.code()}: El correo podría estar registrado", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error de red: ${e.javaClass.simpleName} - ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnRegisterSubmit.isEnabled = true
                }
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*"))
        }
        filePickerLauncher.launch(intent)
    }

    private fun validateForm(): Boolean {
        val nombre = binding.etNombre.text.toString()
        val matricula = binding.etMatricula.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (nombre.isEmpty() || matricula.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        val dominiosValidos = email.endsWith("@uat.edu.mx") || email.endsWith("@alumnos.uat.edu.mx")
        if (!dominiosValidos) {
            Toast.makeText(this, "Usa tu correo institucional (@uat.edu.mx o @alumnos.uat.edu.mx)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 8) {
            Toast.makeText(this, "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Por favor sube tu ficha de pago", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!binding.cbTerms.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}


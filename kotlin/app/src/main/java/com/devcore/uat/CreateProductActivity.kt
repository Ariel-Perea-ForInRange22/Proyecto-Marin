package com.devcore.uat

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityCreateProductBinding
import com.devcore.uat.network.ProductoCreateRequest
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class CreateProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProductBinding
    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.llNoImagePlaceholder.visibility = View.GONE
            Glide.with(this).load(uri).into(binding.ivProductPreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cvImageSelector.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        // Setup Spinners
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf(
            "libros", "electronicos", "ropa", "accesorios", "otros"
        ))
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = catAdapter

        val condAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf(
            "nuevo", "como_nuevo", "usado"
        ))
        condAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCondicion.adapter = condAdapter

        binding.btnPublicar.setOnClickListener {
            crearProducto()
        }
    }

    private fun crearProducto() {
        val titulo = binding.etTitulo.text.toString().trim()
        val desc = binding.etDescripcion.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()
        val cat = binding.spinnerCategoria.selectedItem.toString()
        val cond = binding.spinnerCondicion.selectedItem.toString()

        if (titulo.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Título y precio requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        if (precio == null) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }

        binding.overlayLoading.visibility = View.VISIBLE

        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull()
            if (token == null) {
                Toast.makeText(this@CreateProductActivity, "Sesión expirada", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            try {
                // Paso 1: Crear el producto (textos)
                val req = ProductoCreateRequest(titulo, desc.ifEmpty { null }, precio, cat, cond)
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.crearProducto("Bearer $token", req)
                }

                if (response.isSuccessful && response.body() != null) {
                    val productoId = response.body()!!.id

                    // Paso 2: Subir imagen si hay alguna
                    if (selectedImageUri != null) {
                        subirImagen(productoId, token)
                    } else {
                        binding.overlayLoading.visibility = View.GONE
                        Toast.makeText(this@CreateProductActivity, "Producto publicado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    binding.overlayLoading.visibility = View.GONE
                    Toast.makeText(this@CreateProductActivity, "Error al crear producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.overlayLoading.visibility = View.GONE
                Toast.makeText(this@CreateProductActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun subirImagen(productoId: Int, token: String) {
        val uri = selectedImageUri ?: return
        
        // Copiar el contenido de la URI a un archivo temporal para enviarlo
        val file = getFileFromUri(uri)
        if (file == null) {
            withContext(Dispatchers.Main) {
                binding.overlayLoading.visibility = View.GONE
                Toast.makeText(this@CreateProductActivity, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                finish() // Igual terminamos porque el producto ya se creó
            }
            return
        }

        // Fix para compatibilidad con OkHttp 3.14.x
        val mediaType = MediaType.parse("image/*")
        if (mediaType == null) {
            withContext(Dispatchers.Main) {
                binding.overlayLoading.visibility = View.GONE
                Toast.makeText(this@CreateProductActivity, "Tipo de archivo inválido", Toast.LENGTH_SHORT).show()
                finish()
            }
            return
        }
        val reqFile = RequestBody.create(mediaType, file)
        val body = MultipartBody.Part.createFormData("file", file.name, reqFile)

        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.uploadImagenProducto("Bearer $token", productoId, body)
            }
            withContext(Dispatchers.Main) {
                binding.overlayLoading.visibility = View.GONE
                Toast.makeText(this@CreateProductActivity, "Producto publicado con imagen", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.overlayLoading.visibility = View.GONE
                Toast.makeText(this@CreateProductActivity, "Producto creado, pero falló la imagen: \${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

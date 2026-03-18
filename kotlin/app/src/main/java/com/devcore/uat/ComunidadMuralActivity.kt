package com.devcore.uat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.adapters.PublicacionAdapter
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityComunidadMuralBinding
import com.devcore.uat.network.PublicacionComunidadCreate
import com.devcore.uat.network.PublicacionEstadoUpdate
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComunidadMuralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComunidadMuralBinding
    private lateinit var adapter: PublicacionAdapter
    private lateinit var sessionManager: SessionManager
    
    private var comunidadId: Int = 1 // Por defecto 1
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComunidadMuralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        
        comunidadId = intent.getIntExtra("COMUNIDAD_ID", 1)
        val comunidadNombre = intent.getStringExtra("COMUNIDAD_NOMBRE") ?: "Comunidad"
        
        binding.tvCommunityName.text = comunidadNombre
        
        setupRecyclerView()
        setupListeners()
        loadTokenAndFetchPosts()
    }
    
    private fun setupRecyclerView() {
        adapter = PublicacionAdapter(
            emptyList(),
            onApproveClick = { post ->
                cambiarEstadoPost(post.id, "APROBADA")
            },
            onRejectClick = { post ->
                cambiarEstadoPost(post.id, "RECHAZADA")
            },
            onUserClick = { userId, nombre, email ->
                // Opcional: Navegar al perfil al hacer click en el avatar/nombre
            }
        )
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = adapter
    }
    
    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchPosts()
        }
        
        binding.btnSubmitPost.setOnClickListener {
            val content = binding.etPostContent.text.toString().trim()
            if (content.isNotEmpty()) {
                submitPost(content)
            } else {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadTokenAndFetchPosts() {
        lifecycleScope.launch {
            token = sessionManager.authTokenFlow.firstOrNull() ?: ""
            if (token.isEmpty()) {
                Toast.makeText(this@ComunidadMuralActivity, "No hay sesión", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            binding.swipeRefreshLayout.isRefreshing = true
            fetchPosts()
        }
    }
    
    private fun fetchPosts() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.obtenerPublicaciones(comunidadId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.submitList(response.body()!!)
                    } else {
                        Toast.makeText(this@ComunidadMuralActivity, "Error cargando posts", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }
    
    private fun submitPost(content: String) {
        binding.btnSubmitPost.isEnabled = false
        val request = PublicacionComunidadCreate(contenido = content)
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val response = RetrofitClient.apiService.crearPublicacion(authHeader, comunidadId, request)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        binding.etPostContent.text.clear()
                        Toast.makeText(this@ComunidadMuralActivity, "Publicación enviada. Pendiente de revisión.", Toast.LENGTH_LONG).show()
                        fetchPosts() // Recargar para ver el nuevo post u otros
                    } else {
                        Toast.makeText(this@ComunidadMuralActivity, "Error al publicar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnSubmitPost.isEnabled = true
                }
            }
        }
    private fun cambiarEstadoPost(pubId: Int, nuevoEstado: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val request = PublicacionEstadoUpdate(nuevoEstado)
                val response = RetrofitClient.apiService.cambiarEstadoPublicacion(authHeader, comunidadId, pubId, request)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ComunidadMuralActivity, "Post $nuevoEstado", Toast.LENGTH_SHORT).show()
                        fetchPosts() // Recargar feed
                    } else {
                        Toast.makeText(this@ComunidadMuralActivity, "Error de permisos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

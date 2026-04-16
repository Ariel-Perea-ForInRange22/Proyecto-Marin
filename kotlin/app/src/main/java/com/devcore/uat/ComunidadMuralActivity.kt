package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.adapters.PublicacionAdapter
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityComunidadMuralBinding
import com.devcore.uat.network.PublicacionComunidad
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

    private var comunidadId: Int = 1
    private var token: String = ""
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComunidadMuralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        comunidadId = intent.getIntExtra("COMUNIDAD_ID", 1)
        val comunidadNombre = intent.getStringExtra("COMUNIDAD_NOMBRE") ?: "Comunidad"
        binding.tvCommunityName.text = comunidadNombre

        setupListeners()
        loadTokenAndFetch()
    }

    private fun setupRecyclerView() {
        adapter = PublicacionAdapter(
            emptyList(),
            onApproveClick = { post -> cambiarEstadoPost(post.id, "APROBADA") },
            onRejectClick = { post -> cambiarEstadoPost(post.id, "RECHAZADA") },
            onUserClick = { _, _, _ -> },
            currentUserId = currentUserId,
            onLikeClick = { post, position -> toggleLike(post, position) },
            onCommentClick = { post -> abrirComentarios(post) },
            onDeleteClick = { post, position -> borrarPost(post, position) }
        )
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSearchMembers.setOnClickListener {
            val intent = Intent(this, MiembrosComunidadActivity::class.java).apply {
                putExtra("COMUNIDAD_ID", comunidadId)
            }
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener { fetchPosts() }

        binding.btnSubmitPost.setOnClickListener {
            val content = binding.etPostContent.text.toString().trim()
            if (content.isNotEmpty()) submitPost(content)
            else Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTokenAndFetch() {
        lifecycleScope.launch {
            token = sessionManager.authTokenFlow.firstOrNull() ?: ""
            currentUserId = sessionManager.userIdFlow.firstOrNull() ?: -1
            if (token.isEmpty()) {
                Toast.makeText(this@ComunidadMuralActivity, "No hay sesión", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            setupRecyclerView()
            binding.swipeRefreshLayout.isRefreshing = true
            fetchPosts()
        }
    }

    private fun fetchPosts() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.obtenerPublicaciones(comunidadId, token = "Bearer $token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.submitList(response.body()!!)
                    } else {
                        Toast.makeText(this@ComunidadMuralActivity, "Error cargando posts", Toast.LENGTH_SHORT).show()
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun toggleLike(post: PublicacionComunidad, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.reaccionarPublicacion("Bearer $token", post.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        val updated = post.copy(
                            likes_count = result.likes_count,
                            usuario_ha_reaccionado = result.liked
                        )
                        adapter.updatePostAt(position, updated)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirComentarios(post: PublicacionComunidad) {
        val sheet = ComentariosBottomSheet.newInstance(
            post.id,
            ArrayList(post.comentarios)
        )
        sheet.show(supportFragmentManager, "comentarios")
    }

    private fun borrarPost(post: PublicacionComunidad, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.borrarPublicacion("Bearer $token", post.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        adapter.removePostAt(position)
                        Toast.makeText(this@ComunidadMuralActivity, "Post eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ComunidadMuralActivity, "No tienes permiso", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submitPost(content: String) {
        binding.btnSubmitPost.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.crearPublicacion(
                    "Bearer $token", comunidadId, PublicacionComunidadCreate(contenido = content)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        binding.etPostContent.text.clear()
                        Toast.makeText(this@ComunidadMuralActivity, "Publicación enviada. Pendiente de revisión.", Toast.LENGTH_LONG).show()
                        fetchPosts()
                    } else {
                        Toast.makeText(this@ComunidadMuralActivity, "Error al publicar", Toast.LENGTH_SHORT).show()
                    }
                    binding.btnSubmitPost.isEnabled = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComunidadMuralActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    binding.btnSubmitPost.isEnabled = true
                }
            }
        }
    }

    private fun cambiarEstadoPost(pubId: Int, nuevoEstado: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.cambiarEstadoPublicacion(
                    "Bearer $token", comunidadId, pubId, PublicacionEstadoUpdate(nuevoEstado)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ComunidadMuralActivity, "Post $nuevoEstado", Toast.LENGTH_SHORT).show()
                        fetchPosts()
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

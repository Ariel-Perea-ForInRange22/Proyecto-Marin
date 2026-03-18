package com.devcore.uat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.adapters.PublicacionAdapter
import com.devcore.uat.databinding.ActivityUserProfilePostsBinding
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfilePostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfilePostsBinding
    private lateinit var adapter: PublicacionAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfilePostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", -1)
        val userName = intent.getStringExtra("USER_NAME") ?: "Usuario"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        if (userId == -1) {
            Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvProfileName.text = userName
        binding.tvProfileAvatar.text = userName.firstOrNull()?.uppercase() ?: "U"
        
        // Simulación de rol usando el correo
        if (userEmail.endsWith("@docentes.uat.edu.mx")) {
            binding.tvProfileInfo.text = "Docente / Académico"
        } else if (userEmail.endsWith("@uat.edu.mx")) {
            binding.tvProfileInfo.text = "Estudiante UAT"
        } else {
            binding.tvProfileInfo.text = "Miembro de la Comunidad"
        }

        setupRecyclerView()
        setupListeners()
        fetchUserPosts()
    }

    private fun setupRecyclerView() {
        adapter = PublicacionAdapter(emptyList())
        binding.rvUserPosts.layoutManager = LinearLayoutManager(this)
        binding.rvUserPosts.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun fetchUserPosts() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvUserPosts.visibility = View.GONE
        binding.tvEmptyPosts.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.obtenerPublicacionesUsuario(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val posts = response.body()!!
                        adapter.submitList(posts)
                        
                        if (posts.isEmpty()) {
                            binding.tvEmptyPosts.visibility = View.VISIBLE
                        } else {
                            binding.rvUserPosts.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@UserProfilePostsActivity, "No se pudieron cargar posts", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UserProfilePostsActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}

package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devcore.uat.databinding.ActivityHomeBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.adapters.PublicacionAdapter
import com.devcore.uat.data.SessionManager
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: PublicacionAdapter
    private lateinit var sessionManager: SessionManager
    private var token: String = ""
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)

        setupListeners()
        setupBottomNavigation()
        
        loadTokenAndFetchFeed()
    }

    private fun setupRecyclerView() {
        adapter = PublicacionAdapter(
            emptyList(),
            onApproveClick = { }, 
            onRejectClick = { },
            onUserClick = { _, _, _ -> },
            currentUserId = currentUserId,
            onLikeClick = { post, position ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val resp = RetrofitClient.apiService.reaccionarPublicacion("Bearer $token", post.id)
                        withContext(Dispatchers.Main) {
                            if (resp.isSuccessful && resp.body() != null) {
                                val r = resp.body()!!
                                val updated = post.copy(likes_count = r.likes_count, usuario_ha_reaccionado = r.liked)
                                adapter.updatePostAt(position, updated)
                            }
                        }
                    } catch (_: Exception) {}
                }
            },
            onCommentClick = { post ->
                val sheet = ComentariosBottomSheet.newInstance(post.id, ArrayList(post.comentarios))
                sheet.show(supportFragmentManager, "comentarios")
            },
            onDeleteClick = { post, position ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val resp = RetrofitClient.apiService.borrarPublicacion("Bearer $token", post.id)
                        withContext(Dispatchers.Main) {
                            if (resp.isSuccessful) {
                                adapter.removePostAt(position)
                                Toast.makeText(this@HomeActivity, "Post eliminado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (_: Exception) {}
                }
            }
        )
        binding.rvHomeFeed.layoutManager = LinearLayoutManager(this)
        binding.rvHomeFeed.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefreshHome.setOnRefreshListener {
            fetchFeed()
        }
    }

    private fun loadTokenAndFetchFeed() {
        lifecycleScope.launch {
            token = sessionManager.authTokenFlow.firstOrNull() ?: ""
            currentUserId = sessionManager.userIdFlow.firstOrNull() ?: -1
            if (token.isNotEmpty()) {
                setupRecyclerView()
                binding.swipeRefreshHome.isRefreshing = true
                fetchFeed()
            }
        }
    }

    private fun fetchFeed() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.obtenerFeedGlobal(0, 30, "Bearer $token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.submitList(response.body()!!)
                    } else {
                        Toast.makeText(this@HomeActivity, "Error cargando muro global", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.swipeRefreshHome.isRefreshing = false
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        // Home is already selected (current screen)

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

        binding.root.findViewById<android.view.View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}


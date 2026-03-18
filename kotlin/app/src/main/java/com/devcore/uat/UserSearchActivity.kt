package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.adapters.UserSearchAdapter
import com.devcore.uat.databinding.ActivityUserSearchBinding
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSearchBinding
    private lateinit var adapter: UserSearchAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = UserSearchAdapter(emptyList()) { user ->
            // Al hacer clic, ir al perfil de ese usuario
            val intent = Intent(this, UserProfilePostsActivity::class.java).apply {
                putExtra("USER_ID", user.id)
                putExtra("USER_NAME", user.nombre)
                putExtra("USER_EMAIL", user.email)
            }
            startActivity(intent)
        }
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearchUser.text.clear()
        }

        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                binding.btnClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                
                searchJob?.cancel()
                if (query.length >= 2) {
                    searchJob = lifecycleScope.launch {
                        delay(500) // Debounce
                        performSearch(query)
                    }
                } else {
                    adapter.submitList(emptyList())
                    binding.tvEmptyState.visibility = View.VISIBLE
                }
            }
        })
    }

    private suspend fun performSearch(query: String) {
        withContext(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
            binding.rvUsers.visibility = View.GONE
        }

        try {
            val response = RetrofitClient.apiService.buscarUsuarios(query)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!
                    adapter.submitList(results)
                    
                    if (results.isEmpty()) {
                        binding.tvEmptyState.text = "No se encontraron resultados"
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvUsers.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@UserSearchActivity, "Error en la búsqueda", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserSearchActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        } finally {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}

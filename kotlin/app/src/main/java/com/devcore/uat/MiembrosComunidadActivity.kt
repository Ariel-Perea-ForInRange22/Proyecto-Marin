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
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityUserSearchBinding
import com.devcore.uat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MiembrosComunidadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSearchBinding
    private lateinit var adapter: UserSearchAdapter
    private lateinit var sessionManager: SessionManager
    private var searchJob: Job? = null
    private var comunidadId: Int = -1
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        comunidadId = intent.getIntExtra("COMUNIDAD_ID", -1)

        if (comunidadId == -1) {
            Toast.makeText(this, "Error: Comunidad no identificada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Personalizar el hint del layout compartido
        binding.etSearchUser.hint = "Buscar miembros en este grupo..."
        binding.tvEmptyState.text = "Cargando miembros..."

        setupRecyclerView()
        setupListeners()
        loadTokenAndFetchMembers()
    }

    private fun setupRecyclerView() {
        adapter = UserSearchAdapter(emptyList()) { user ->
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
                searchJob = lifecycleScope.launch {
                    delay(300)
                    performSearch(query)
                }
            }
        })
    }

    private fun loadTokenAndFetchMembers() {
        lifecycleScope.launch {
            token = sessionManager.authTokenFlow.firstOrNull() ?: ""
            if (token.isEmpty()) {
                Toast.makeText(this@MiembrosComunidadActivity, "Sesión expirada", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            performSearch("") // Cargar todos inicialmente
        }
    }

    private suspend fun performSearch(query: String) {
        withContext(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }

        try {
            val response = RetrofitClient.apiService.buscarMiembrosComunidad(
                "Bearer $token",
                comunidadId,
                query
            )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!
                    adapter.submitList(results)
                    
                    if (results.isEmpty()) {
                        binding.tvEmptyState.text = if (query.isEmpty()) "Aún no hay miembros" else "No se encontraron resultados"
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvUsers.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MiembrosComunidadActivity, "Error al cargar miembros", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MiembrosComunidadActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        } finally {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}

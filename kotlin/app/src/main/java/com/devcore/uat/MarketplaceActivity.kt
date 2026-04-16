package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.adapters.ProductoAdapter
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityMarketplaceBinding
import com.devcore.uat.network.ProductoCreateRequest
import com.devcore.uat.network.ProductoResponse
import com.devcore.uat.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketplaceBinding
    private lateinit var sessionManager: SessionManager
    
    private lateinit var feedAdapter: ProductoAdapter
    private lateinit var misProductosAdapter: ProductoAdapter
    
    private var currentCategory: String? = null
    private var currentQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRecyclerViews()
        setupClickListeners()
        setupSearch()
        
        loadProductos()
        loadMisProductos()
    }

    private fun setupRecyclerViews() {
        // Feed general (Grid de 2 columnas)
        feedAdapter = ProductoAdapter(onItemClick = { producto ->
            showProductDetails(producto)
        })
        binding.rvProductos.apply {
            layoutManager = GridLayoutManager(this@MarketplaceActivity, 2)
            adapter = feedAdapter
        }

        // Mis Productos (Lista horizontal o vertical, usaremos vertical por ahora)
        misProductosAdapter = ProductoAdapter(onItemClick = { producto ->
            showMyProductOptions(producto)
        })
        binding.rvMisProductos.apply {
            layoutManager = LinearLayoutManager(this@MarketplaceActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = misProductosAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Categorías
        val catButtons = mapOf(
            binding.btnCatTodos to null,
            binding.btnCatLibros to "libros",
            binding.btnCatElectronicos to "electronicos",
            binding.btnCatRopa to "ropa",
            binding.btnCatAccesorios to "accesorios"
        )

        for ((btn, cat) in catButtons) {
            btn.setOnClickListener {
                currentCategory = cat
                loadProductos()
            }
        }

        // FAB Publicar
        binding.fabPublicar.setOnClickListener {
            showPublishActivity()
        }

        // Bottom Navigation
        binding.root.findViewById<android.view.View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navBus)?.setOnClickListener {
            startActivity(Intent(this, BusTrackingActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navComunidades)?.setOnClickListener {
            startActivity(Intent(this, ComunidadesActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)); finish()
        }
    }

    private fun setupSearch() {
        binding.btnBuscar.setOnClickListener {
            currentQuery = binding.etBuscar.text.toString().trim().ifEmpty { null }
            loadProductos()
        }

        // Opcional: buscar presionado "Enter" en teclado
        binding.etBuscar.setOnEditorActionListener { _, _, _ ->
            currentQuery = binding.etBuscar.text.toString().trim().ifEmpty { null }
            loadProductos()
            true
        }
        
        // Limpiar busqueda
        binding.etBuscar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty() && currentQuery != null) {
                    currentQuery = null
                    loadProductos()
                }
            }
        })
    }

    // ─────────────────────────────────────────────
    //  Llamadas al Backend
    // ─────────────────────────────────────────────

    private fun loadProductos() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvProductos.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.tvResultsCount.text = "Cargando..."

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.listarProductos(
                        skip = 0,
                        limit = 50,
                        categoria = currentCategory,
                        query = currentQuery
                    )
                }

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        val productos = response.body()!!
                        
                        binding.tvResultsCount.text = "${productos.size} resultados"
                        
                        if (productos.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                        } else {
                            binding.rvProductos.visibility = View.VISIBLE
                            feedAdapter.updateData(productos)
                        }
                    } else {
                        Toast.makeText(this@MarketplaceActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                        binding.tvResultsCount.text = "Error al cargar"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvResultsCount.text = "Error de conexión"
                    Toast.makeText(this@MarketplaceActivity, "Revisa tu conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadMisProductos() {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull() ?: return@launch
            
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.misProductos("Bearer $token")
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val misProductos = response.body()!!
                        if (misProductos.isNotEmpty()) {
                            binding.tvMisProductosHeader.visibility = View.VISIBLE
                            binding.rvMisProductos.visibility = View.VISIBLE
                            misProductosAdapter.updateData(misProductos)
                        } else {
                            binding.tvMisProductosHeader.visibility = View.GONE
                            binding.rvMisProductos.visibility = View.GONE
                        }
                    }
                }
            } catch (e: Exception) {
                // Silencioso
            }
        }
    }

    // ─────────────────────────────────────────────
    //  Publicar nuevo producto
    // ─────────────────────────────────────────────

    private fun showPublishActivity() {
        startActivity(Intent(this, CreateProductActivity::class.java))
    }

    // ─────────────────────────────────────────────
    //  Detalles e interacciones
    // ─────────────────────────────────────────────

    private fun showProductDetails(producto: ProductoResponse) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("titulo", producto.titulo)
            putExtra("descripcion", producto.descripcion)
            putExtra("precio", producto.precio)
            putExtra("categoria", producto.categoria)
            putExtra("condicion", producto.condicion)
            putExtra("vendedor_nombre", producto.vendedor?.nombre)
            putExtra("imagen_url", producto.imagen_url)
        }
        startActivity(intent)
    }

    private fun showMyProductOptions(producto: ProductoResponse) {
        val options = arrayOf("Eliminar Producto")
        AlertDialog.Builder(this)
            .setTitle(producto.titulo)
            .setItems(options) { _, which ->
                if (which == 0) {
                    confirmarEliminarProducto(producto.id)
                }
            }
            .show()
    }
    
    private fun confirmarEliminarProducto(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar")
            .setMessage("¿Seguro que deseas eliminar este producto?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(id: Int) {
        lifecycleScope.launch {
            val token = sessionManager.authTokenFlow.firstOrNull() ?: return@launch
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.eliminarProducto("Bearer $token", id)
                }
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MarketplaceActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        loadProductos()
                        loadMisProductos()
                    } else {
                        Toast.makeText(this@MarketplaceActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MarketplaceActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

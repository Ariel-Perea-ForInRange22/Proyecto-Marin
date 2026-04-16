package com.devcore.uat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devcore.uat.databinding.ActivityProductDetailBinding
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        // Recibir datos del Intent
        val titulo = intent.getStringExtra("titulo") ?: "Sin Título"
        val descripcion = intent.getStringExtra("descripcion") ?: "Sin descripción"
        val precio = intent.getDoubleExtra("precio", 0.0)
        val categoria = intent.getStringExtra("categoria") ?: "otros"
        val condicion = intent.getStringExtra("condicion") ?: "usado"
        val imagenUrl = intent.getStringExtra("imagen_url")
        val vendedorNombre = intent.getStringExtra("vendedor_nombre") ?: "Vendedor"

        binding.tvProductTitle.text = titulo
        binding.tvProductDescription.text = descripcion
        
        val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        binding.tvProductPrice.text = fmt.format(precio)

        binding.tvProductCategory.text = categoria.replaceFirstChar { it.uppercase() }
        binding.tvProductCondition.text = when (condicion) {
            "nuevo" -> "Nuevo"
            "como_nuevo" -> "Como nuevo"
            "usado" -> "Usado"
            else -> condicion
        }

        binding.tvSellerName.text = vendedorNombre
        binding.tvSellerInitial.text = if (vendedorNombre.isNotEmpty()) vendedorNombre.substring(0, 1).uppercase() else "U"

        // Imagen o Emoji
        if (!imagenUrl.isNullOrEmpty()) {
            binding.ivProductImage.visibility = View.VISIBLE
            binding.tvProductEmoji.visibility = View.GONE
            val fullUrl = "http://10.0.2.2:8000\$imagenUrl" // Para el emulador, usar IP o config global
            Glide.with(this)
                .load(fullUrl)
                .centerCrop()
                .into(binding.ivProductImage)
        } else {
            binding.ivProductImage.visibility = View.GONE
            binding.tvProductEmoji.visibility = View.VISIBLE
            binding.tvProductEmoji.text = getEmojiForCategory(categoria)
        }

        binding.btnContactSeller.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de chat próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEmojiForCategory(cat: String): String {
        return when (cat) {
            "libros" -> "📚"
            "electronicos" -> "💻"
            "ropa" -> "👕"
            "accesorios" -> "🎒"
            else -> "📦"
        }
    }
}

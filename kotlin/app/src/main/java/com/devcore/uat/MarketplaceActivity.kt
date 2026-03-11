package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devcore.uat.databinding.ActivityMarketplaceBinding

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketplaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnBuscar.setOnClickListener {
            val query = binding.etBuscar.text?.toString() ?: ""
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Buscando: $query", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabPublicar.setOnClickListener {
            Toast.makeText(this, "Publicar artículo – Próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnEditarPublicacion.setOnClickListener {
            Toast.makeText(this, "Editar publicación – Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Categorías
        binding.btnCatLibros.setOnClickListener {
            Toast.makeText(this, "Categoría: Libros", Toast.LENGTH_SHORT).show()
        }
        binding.btnCatElectronicos.setOnClickListener {
            Toast.makeText(this, "Categoría: Electrónicos", Toast.LENGTH_SHORT).show()
        }
        binding.btnCatRopa.setOnClickListener {
            Toast.makeText(this, "Categoría: Ropa", Toast.LENGTH_SHORT).show()
        }
        binding.btnCatAccesorios.setOnClickListener {
            Toast.makeText(this, "Categoría: Accesorios", Toast.LENGTH_SHORT).show()
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
}

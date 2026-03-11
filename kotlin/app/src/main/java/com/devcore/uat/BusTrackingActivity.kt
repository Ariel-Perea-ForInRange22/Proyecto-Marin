package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devcore.uat.databinding.ActivityBusTrackingBinding

class BusTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusTrackingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        val rutas = listOf(
            "Ruta 1 – Centro – Victoria",
            "Ruta 2 – Norte – Matamoros",
            "Ruta 3 – Sur – Tampico",
            "Ruta 4 – Oriente – Reynosa"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rutas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRuta.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnYaPaso.setOnClickListener {
            val ruta = binding.spinnerRuta.selectedItem?.toString() ?: "la ruta seleccionada"
            Toast.makeText(this, "✅ Reporte enviado: $ruta ya pasó", Toast.LENGTH_SHORT).show()
        }

        binding.btnNoPaso.setOnClickListener {
            val ruta = binding.spinnerRuta.selectedItem?.toString() ?: "la ruta seleccionada"
            Toast.makeText(this, "❌ Reporte enviado: $ruta no ha pasado", Toast.LENGTH_SHORT).show()
        }

        // Bottom Navigation
        binding.root.findViewById<android.view.View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navMarket)?.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navComunidades)?.setOnClickListener {
            startActivity(Intent(this, ComunidadesActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)); finish()
        }
    }
}

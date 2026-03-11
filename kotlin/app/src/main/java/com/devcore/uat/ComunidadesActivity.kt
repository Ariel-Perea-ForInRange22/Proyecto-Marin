package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devcore.uat.databinding.ActivityComunidadesBinding

class ComunidadesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComunidadesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComunidadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        binding.fabCreateCommunity.setOnClickListener {
            Toast.makeText(this, "Crear nueva comunidad (Próximamente)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.root.findViewById<android.view.View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        
        binding.root.findViewById<android.view.View>(R.id.navBus)?.setOnClickListener {
            startActivity(Intent(this, BusTrackingActivity::class.java))
            finish()
        }

        binding.root.findViewById<android.view.View>(R.id.navMarket)?.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java))
            finish()
        }

        binding.root.findViewById<android.view.View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}

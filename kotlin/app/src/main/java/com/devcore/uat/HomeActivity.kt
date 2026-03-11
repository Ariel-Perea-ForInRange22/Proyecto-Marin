package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devcore.uat.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        binding.cardBusTracking.setOnClickListener {
            startActivity(Intent(this, BusTrackingActivity::class.java))
        }

        binding.cardMarketplace.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java))
        }

        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.cardComunidades.setOnClickListener {
            startActivity(Intent(this, ComunidadesActivity::class.java))
        }

        binding.btnCheckIn.setOnClickListener {
            Toast.makeText(this, "¡Check-in completado! +5 puntos", Toast.LENGTH_SHORT).show()
        }

        binding.btnReport.setOnClickListener {
            startActivity(Intent(this, BusTrackingActivity::class.java))
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


package com.devcore.uat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devcore.uat.databinding.ActivityComunidadesBinding

class ComunidadesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComunidadesBinding

    private data class CommunityInfo(val id: Int, val name: String, val members: Int)

    private val communities = listOf(
        CommunityInfo(1, "Programación UAT", 128),
        CommunityInfo(2, "Avisos Generales UAT", 512),
        CommunityInfo(3, "Egresados UAT 2025", 89)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComunidadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun openDashboard(index: Int) {
        val c = communities[index]
        val intent = Intent(this, CommunityDashboardActivity::class.java).apply {
            putExtra("COMMUNITY_ID", c.id)
            putExtra("COMMUNITY_NAME", c.name)
            putExtra("MEMBER_COUNT", c.members)
        }
        startActivity(intent)
    }

    private fun setupClickListeners() {
        binding.cardComunidad1.setOnClickListener { openDashboard(0) }
        binding.cardComunidad2.setOnClickListener { openDashboard(1) }
        binding.cardComunidad3.setOnClickListener { openDashboard(2) }

        binding.fabCreateCommunity.setOnClickListener {
            Toast.makeText(this, "Crear nueva comunidad — Próximamente", Toast.LENGTH_SHORT).show()
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

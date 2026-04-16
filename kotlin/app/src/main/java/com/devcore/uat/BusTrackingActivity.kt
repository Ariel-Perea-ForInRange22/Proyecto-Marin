package com.devcore.uat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityBusTrackingBinding
import com.devcore.uat.network.RetrofitClient
import com.devcore.uat.network.ReporteBusCreate
import com.devcore.uat.network.UbicacionCreate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import android.graphics.Color

class BusTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusTrackingBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var reporteAdapter: BusReporteAdapter

    private val PERMISSION_REQUEST_LOCATION = 1001
    
    // UAT Tampico Campus aprox coord
    private val START_POINT = GeoPoint(22.2541, -97.8687)

    private var locationManager: LocationManager? = null
    private var isSharingLocation = false
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var refreshJob: Job? = null
    private var locationJob: Job? = null
    
    private val heatmapMarkers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // OSMDroid configuration
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName
        
        binding = ActivityBusTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)

        setupMap()
        setupRecyclerView()
        setupClickListeners()
        
        startRefreshingData()
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        val mapController = binding.mapView.controller
        mapController.setZoom(15.5)
        mapController.setCenter(START_POINT)
    }

    private fun setupRecyclerView() {
        reporteAdapter = BusReporteAdapter(emptyList()) { reporte ->
            confirmarReporte(reporte.id)
        }
        binding.rvReportesBus.layoutManager = LinearLayoutManager(this)
        binding.rvReportesBus.adapter = reporteAdapter
    }

    private fun setupClickListeners() {
        binding.mapView.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true) // Permite mover el mapa sin que la pantalla haga scroll
            false
        }

        binding.switchUbicacion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermissionAndStartSharing()
            } else {
                stopSharingLocation()
            }
        }

        binding.btnYaPaso.setOnClickListener {
            mostrarDialogoReporte("ya_paso")
        }

        binding.btnNoPaso.setOnClickListener {
            mostrarDialogoReporte("no_paso")
        }

        // Bottom Navigation
        binding.root.findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)); finish()
        }
        binding.root.findViewById<View>(R.id.navMarket)?.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java)); finish()
        }
        binding.root.findViewById<View>(R.id.navComunidades)?.setOnClickListener {
            startActivity(Intent(this, ComunidadesActivity::class.java)); finish()
        }
        binding.root.findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)); finish()
        }
    }

    private fun mostrarDialogoReporte(tipo: String) {
        val zonas = arrayOf("FIT", "FADU", "Facultad de Medicina", "Odontología", "FADIX")
        var zonaSeleccionada = zonas[0]

        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (tipo == "ya_paso") "Confirmar: Ya pasó" else "Confirmar: No ha pasado")
        builder.setSingleChoiceItems(zonas, 0) { _, which ->
            zonaSeleccionada = zonas[which]
        }

        builder.setPositiveButton("Reportar") { dialog, _ ->
            enviarReporteBackend(tipo, zonaSeleccionada)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun enviarReporteBackend(tipo: String, zona: String) {
        scope.launch {
            try {
                val authHeader = "Bearer ${sessionManager.authTokenFlow.first() ?: ""}"
                
                // Intenta obtener GPS actual si se tiene permiso
                var lat: Double? = null
                var lng: Double? = null
                if (ContextCompat.checkSelfPermission(this@BusTrackingActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (loc != null) {
                        lat = loc.latitude
                        lng = loc.longitude
                    }
                }

                val req = ReporteBusCreate(tipo = tipo, zona = if(zona.isBlank()) null else zona, latitud = lat, longitud = lng)
                val response = RetrofitClient.apiService.crearReporteBus(authHeader, req)
                if (response.isSuccessful) {
                    Toast.makeText(this@BusTrackingActivity, "Reporte enviado con éxito", Toast.LENGTH_SHORT).show()
                    cargarReportesRecientes() // refrescar rápido
                } else {
                    Toast.makeText(this@BusTrackingActivity, "Error al reportar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BusTrackingActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarReporte(reporteId: Int) {
        scope.launch {
            try {
                val authHeader = "Bearer ${sessionManager.authTokenFlow.first() ?: ""}"
                val response = RetrofitClient.apiService.confirmarReporteBus(authHeader, reporteId)
                if (response.isSuccessful) {
                    Toast.makeText(this@BusTrackingActivity, "¡Confirmado!", Toast.LENGTH_SHORT).show()
                    cargarReportesRecientes() // refrescar
                } else {
                    Toast.makeText(this@BusTrackingActivity, "Posiblemente no puedas confirmar tu propio reporte", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BusTrackingActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==========================================
    // REFRESH DATA (Puntos Calor + Reportes)
    // ==========================================
    private fun startRefreshingData() {
        refreshJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                withContext(Dispatchers.Main) {
                    cargarPuntosCalor()
                    cargarReportesRecientes()
                }
                delay(30000) // 30 segundos
            }
        }
    }

    private fun cargarPuntosCalor() {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerPuntosCalor()
                if (response.isSuccessful && response.body() != null) {
                    val puntos = response.body()!!
                    
                    // Limpiar marcadores viejos
                    for (m in heatmapMarkers) {
                        binding.mapView.overlays.remove(m)
                    }
                    heatmapMarkers.clear()
                    
                    // Agregar nuevos
                    for (p in puntos) {
                        val marker = Marker(binding.mapView)
                        marker.position = GeoPoint(p.latitud, p.longitud)
                        marker.title = "${p.cantidad} alumno(s) aquí"
                        // Usar el pin por defecto de OSMDroid
                        // binding.mapView.overlays.add(marker)
                        binding.mapView.overlays.add(marker)
                        heatmapMarkers.add(marker)
                    }
                    binding.mapView.invalidate()
                }
            } catch (e: Exception) {
                // Ignore network error on quiet refresh
            }
        }
    }

    private fun cargarReportesRecientes() {
        scope.launch {
            try {
                binding.pbReportes.visibility = View.VISIBLE
                val response = RetrofitClient.apiService.obtenerReportesBus()
                if (response.isSuccessful && response.body() != null) {
                    val reportes = response.body()!!
                    reporteAdapter.updateData(reportes)
                    
                    if (reportes.isEmpty()) {
                        binding.tvNoReportes.visibility = View.VISIBLE
                        binding.rvReportesBus.visibility = View.GONE
                    } else {
                        binding.tvNoReportes.visibility = View.GONE
                        binding.rvReportesBus.visibility = View.VISIBLE
                    }
                }
                binding.pbReportes.visibility = View.GONE
            } catch (e: Exception) {
                binding.pbReportes.visibility = View.GONE
            }
        }
    }


    // ==========================================
    // GPS & LOCATION SHARING
    // ==========================================
    private fun checkLocationPermissionAndStartSharing() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_LOCATION)
        } else {
            startSharingLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSharingLocation()
            } else {
                Toast.makeText(this, "Se requiere permiso de ubicación para compartir tu status.", Toast.LENGTH_LONG).show()
                binding.switchUbicacion.isChecked = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startSharingLocation() {
        isSharingLocation = true
        Toast.makeText(this, "Compartiendo ubicación de forma anónima...", Toast.LENGTH_SHORT).show()
        
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Empezar a enviar cada 2 minutos
        locationJob = scope.launch(Dispatchers.IO) {
            while (isActive && isSharingLocation) {
                withContext(Dispatchers.Main) {
                    val loc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER) 
                           ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    
                    if (loc != null) {
                        enviarUbicacionActivaBackend(loc.latitude, loc.longitude)
                    }
                }
                delay(120000) // 2 minutos
            }
        }
    }

    private fun stopSharingLocation() {
        isSharingLocation = false
        locationJob?.cancel()
        
        // Notificar backend para de-activar
        scope.launch {
            try {
                val authHeader = "Bearer ${sessionManager.authTokenFlow.first() ?: ""}"
                RetrofitClient.apiService.borrarUbicacionActiva(authHeader)
            } catch (e: Exception) {}
        }
    }

    private fun enviarUbicacionActivaBackend(lat: Double, lng: Double) {
        scope.launch {
            try {
                val authHeader = "Bearer ${sessionManager.authTokenFlow.first() ?: ""}"
                RetrofitClient.apiService.reportarUbicacionActiva(authHeader, UbicacionCreate(lat, lng))
            } catch (e: Exception) {}
        }
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isSharingLocation) {
            stopSharingLocation() // manda borrar
        }
        refreshJob?.cancel()
        locationJob?.cancel()
    }
}

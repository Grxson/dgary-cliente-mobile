package com.tuempresa.tuapp.ui.account.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tuempresa.tuapp.R
import java.io.IOException
import java.util.Locale

class AddressPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btnBack: ImageView
    private lateinit var btnSaveAddress: Button
    private lateinit var etAddressTitle: EditText
    private lateinit var tvSelectedAddress: TextView
    
    private var googleMap: GoogleMap? = null
    private var selectedMarker: Marker? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private var selectedAddress: String? = null
    private val geocoder by lazy { Geocoder(this, Locale.getDefault()) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Default location (México - Guadalajara)
    companion object {
        private const val DEFAULT_LAT = 20.6597
        private const val DEFAULT_LNG = -103.3496
        private const val ZOOM_LEVEL = 15f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        const val EXTRA_ADDRESS_TITLE = "address_title"
        const val EXTRA_ADDRESS_LATITUDE = "address_latitude"
        const val EXTRA_ADDRESS_LONGITUDE = "address_longitude"
        const val EXTRA_ADDRESS_STRING = "address_string"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_picker)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initializeViews()
        setupListeners()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_address_picker) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Si viene de editar, cargar datos previos
        val previousTitle = intent.getStringExtra(EXTRA_ADDRESS_TITLE)
        if (!previousTitle.isNullOrEmpty()) {
            etAddressTitle.setText(previousTitle)
            val prevLat = intent.getDoubleExtra(EXTRA_ADDRESS_LATITUDE, DEFAULT_LAT)
            val prevLng = intent.getDoubleExtra(EXTRA_ADDRESS_LONGITUDE, DEFAULT_LNG)
            selectedLatitude = prevLat
            selectedLongitude = prevLng
            selectedAddress = intent.getStringExtra(EXTRA_ADDRESS_STRING) ?: ""
        }

        // Solicitar permisos de ubicación
        requestLocationPermission()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_address_picker)
        btnSaveAddress = findViewById(R.id.btn_save_address)
        etAddressTitle = findViewById(R.id.et_address_title)
        tvSelectedAddress = findViewById(R.id.tv_selected_address)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSaveAddress.setOnClickListener {
            saveAddress()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    // Usar ubicación default si no se otorga permiso
                    Toast.makeText(
                        this,
                        getString(R.string.account_permiso_ubicacion_denegado),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null && selectedLatitude == null) {
                        // Solo usar ubicación actual si no hay previa
                        selectedLatitude = location.latitude
                        selectedLongitude = location.longitude
                        
                        // Actualizar mapa si ya está listo
                        googleMap?.let {
                            val currentLocation = LatLng(location.latitude, location.longitude)
                            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL))
                            if (selectedMarker == null) {
                                placeMarker(currentLocation)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback a ubicación default
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        // Usar ubicación previa si existe
        val initialLat = selectedLatitude ?: DEFAULT_LAT
        val initialLng = selectedLongitude ?: DEFAULT_LNG
        val initialLocation = LatLng(initialLat, initialLng)

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, ZOOM_LEVEL))

        // Si hay ubicación previa, colocar marcador
        if (selectedLatitude != null && selectedLongitude != null) {
            placeMarker(initialLocation)
        }

        // Escuchar clicks en el mapa para colocar marcador
        googleMap?.setOnMapClickListener { latLng ->
            placeMarker(latLng)
        }

        // Intentar obtener ubicación actual
        if (selectedLatitude == null) {
            getCurrentLocation()
        }
    }

    private fun placeMarker(latLng: LatLng) {
        selectedMarker?.remove()
        
        selectedLatitude = latLng.latitude
        selectedLongitude = latLng.longitude

        selectedMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.account_ubicacion_seleccionada))
        )

        // Obtener dirección del punto seleccionado usando Geocoding
        getAddressFromLocation(latLng.latitude, latLng.longitude)

        // Centrar mapa en el marcador
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                selectedAddress = buildAddressString(address)
                tvSelectedAddress.text = selectedAddress
            } else {
                selectedAddress = "Lat: $latitude, Lng: $longitude"
                tvSelectedAddress.text = selectedAddress
            }
        } catch (e: IOException) {
            selectedAddress = "Lat: $latitude, Lng: $longitude"
            tvSelectedAddress.text = selectedAddress
        }
    }

    private fun buildAddressString(address: android.location.Address): String {
        val parts = mutableListOf<String>()
        
        // Calle y número
        if (!address.thoroughfare.isNullOrEmpty()) {
            parts.add(address.thoroughfare)
        }
        
        // Ciudad
        if (!address.locality.isNullOrEmpty()) {
            parts.add(address.locality)
        }
        
        // Código postal
        if (!address.postalCode.isNullOrEmpty()) {
            parts.add(address.postalCode)
        }

        return parts.joinToString(", ")
    }

    private fun saveAddress() {
        val title = etAddressTitle.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.account_ingresa_nombre),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedLatitude == null || selectedLongitude == null || selectedAddress.isNullOrEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.account_selecciona_ubicacion),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Retornar los datos a SettingsActivity
        val resultIntent = Intent().apply {
            putExtra(EXTRA_ADDRESS_TITLE, title)
            putExtra(EXTRA_ADDRESS_LATITUDE, selectedLatitude!!)
            putExtra(EXTRA_ADDRESS_LONGITUDE, selectedLongitude!!)
            putExtra(EXTRA_ADDRESS_STRING, selectedAddress!!)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}

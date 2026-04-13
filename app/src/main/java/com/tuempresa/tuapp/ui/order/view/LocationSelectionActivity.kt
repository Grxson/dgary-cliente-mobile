package com.tuempresa.tuapp.ui.order.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.button.MaterialButton
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.local.AddressStore
import com.tuempresa.tuapp.domain.model.SavedAddress
import java.util.Locale

class LocationSelectionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btnBack: ImageView
    private lateinit var tvSelectedAddress: TextView
    private lateinit var tvSavedAddress: TextView
    private lateinit var btnUseMyLocation: MaterialButton
    private lateinit var btnUseSavedAddress: MaterialButton
    private lateinit var btnConfirmLocation: MaterialButton

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val geocoder by lazy { Geocoder(this, Locale.getDefault()) }
    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null
    private var selectedAddress: String? = null
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null
    private var primarySavedAddress: SavedAddress? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 200
        private const val DEFAULT_LAT = 20.6597
        private const val DEFAULT_LNG = -103.3496
        private const val ZOOM_LEVEL = 16f

        const val EXTRA_SELECTED_ADDRESS = "selected_address"
        const val EXTRA_SELECTED_LAT = "selected_lat"
        const val EXTRA_SELECTED_LNG = "selected_lng"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_selection)

        btnBack = findViewById(R.id.btn_back_location)
        tvSelectedAddress = findViewById(R.id.tv_selected_delivery_address)
        tvSavedAddress = findViewById(R.id.tv_saved_address_value)
        btnUseMyLocation = findViewById(R.id.btn_use_my_location)
        btnUseSavedAddress = findViewById(R.id.btn_use_saved_address)
        btnConfirmLocation = findViewById(R.id.btn_confirm_delivery_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        primarySavedAddress = AddressStore.getPrimaryAddress(this)

        tvSavedAddress.text = primarySavedAddress?.direccion ?: getString(R.string.location_no_saved)
        btnUseSavedAddress.isEnabled = primarySavedAddress?.latitude != null && primarySavedAddress?.longitude != null

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_location_selection) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnBack.setOnClickListener { finish() }
        btnUseMyLocation.setOnClickListener { moveToCurrentLocation(forceMarker = true) }
        btnUseSavedAddress.setOnClickListener { useSavedAddress() }
        btnConfirmLocation.setOnClickListener { confirmLocation() }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.setOnMapClickListener { latLng ->
            placeMarker(latLng.latitude, latLng.longitude, null)
        }

        if (!useSavedAddress()) {
            moveToCurrentLocation(forceMarker = true)
        }
    }

    private fun useSavedAddress(): Boolean {
        val saved = primarySavedAddress
        val lat = saved?.latitude
        val lng = saved?.longitude
        if (saved == null || lat == null || lng == null) {
            return false
        }

        placeMarker(lat, lng, saved.direccion)
        return true
    }

    private fun moveToCurrentLocation(forceMarker: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val addressFromGeo = reverseGeocode(location.latitude, location.longitude)
                if (forceMarker) {
                    placeMarker(location.latitude, location.longitude, addressFromGeo)
                } else {
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            ZOOM_LEVEL
                        )
                    )
                }
            } else {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(DEFAULT_LAT, DEFAULT_LNG), ZOOM_LEVEL))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            moveToCurrentLocation(forceMarker = true)
        }
    }

    private fun placeMarker(lat: Double, lng: Double, knownAddress: String?) {
        val latLng = LatLng(lat, lng)
        marker?.remove()
        marker = googleMap?.addMarker(MarkerOptions().position(latLng).title(getString(R.string.account_ubicacion_seleccionada)))

        selectedLat = lat
        selectedLng = lng
        selectedAddress = knownAddress ?: reverseGeocode(lat, lng)

        tvSelectedAddress.text = selectedAddress ?: getString(R.string.location_select_on_map)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))
    }

    private fun reverseGeocode(lat: Double, lng: Double): String {
        return try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses.isNullOrEmpty()) {
                "Lat: $lat, Lng: $lng"
            } else {
                val address = addresses[0]
                listOfNotNull(address.thoroughfare, address.locality, address.postalCode)
                    .joinToString(", ")
                    .ifBlank { "Lat: $lat, Lng: $lng" }
            }
        } catch (_: Exception) {
            "Lat: $lat, Lng: $lng"
        }
    }

    private fun confirmLocation() {
        val address = selectedAddress
        val lat = selectedLat
        val lng = selectedLng

        if (address.isNullOrBlank() || lat == null || lng == null) {
            tvSelectedAddress.text = getString(R.string.location_address_required)
            return
        }

        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_ADDRESS, address)
            putExtra(EXTRA_SELECTED_LAT, lat)
            putExtra(EXTRA_SELECTED_LNG, lng)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}


package com.tuempresa.tuapp.ui.order.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.remote.dto.OrderDetailFullDto
import com.tuempresa.tuapp.data.remote.dto.OrderTrackingDto
import com.tuempresa.tuapp.data.repository.DeliveryRepository
import com.tuempresa.tuapp.data.repository.OrderRepository
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class OrderTrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btnClose: ImageView
    private lateinit var tvEstado: TextView
    private lateinit var tvHoraLlegada: TextView
    private lateinit var pbProgreso: ProgressBar
    private lateinit var tvHoraEntrega: TextView
    private lateinit var tvTrackingLastUpdate: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvItemsTracking: TextView
    private lateinit var mapWaitingOverlay: FrameLayout

    private val orderRepository by lazy { OrderRepository(this) }
    private val deliveryRepository by lazy { DeliveryRepository(this) }
    private val httpClient = OkHttpClient()

    private var orderId: String = ""
    private var orderDetail: OrderDetailFullDto? = null
    private var trackingInfo: OrderTrackingDto? = null
    private var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var routePolyline: Polyline? = null
    private var refreshJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        initializeViews()
        setupListeners()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_tracking) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadTrackingData()
    }

    override fun onDestroy() {
        refreshJob?.cancel()
        routePolyline?.remove()
        driverMarker?.remove()
        destinationMarker?.remove()
        super.onDestroy()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        renderTrackingOnMap()
    }

    private fun initializeViews() {
        btnClose = findViewById(R.id.btn_close_tracking)
        tvEstado = findViewById(R.id.tv_estado)
        tvHoraLlegada = findViewById(R.id.tv_hora_llegada)
        pbProgreso = findViewById(R.id.pb_progreso)
        tvHoraEntrega = findViewById(R.id.tv_hora_entrega)
        tvTrackingLastUpdate = findViewById(R.id.tv_tracking_last_update)
        tvDireccion = findViewById(R.id.tv_direccion_tracking)
        tvTotal = findViewById(R.id.tv_total_tracking)
        tvItemsTracking = findViewById(R.id.tv_items_tracking)
        mapWaitingOverlay = findViewById(R.id.map_waiting_overlay)
    }

    private fun setupListeners() {
        btnClose.setOnClickListener { finish() }
    }

    private fun loadTrackingData() {
        orderId = intent.getStringExtra("order_id")
            ?: intent.getIntExtra("order_id", 0).takeIf { it > 0 }?.toString()
            ?: ""

        if (orderId.isBlank()) {
            showErrorState(getString(R.string.order_tracking_error))
            return
        }

        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            showLoadingState()

            while (isActive) {
                fetchTrackingSnapshot()
                delay(15_000)
            }
        }
    }

    private suspend fun fetchTrackingSnapshot() {
        val order = orderDetail ?: orderRepository.getOrder(orderId).also {
            if (it != null) {
                orderDetail = it
            }
        }

        val tracking = deliveryRepository.getOrderTracking(orderId)
        if (tracking != null) {
            trackingInfo = tracking
        }

        if (order == null && tracking == null) {
            showErrorState(getString(R.string.order_tracking_error))
            return
        }

        renderTracking(order, tracking)
    }

    private fun showLoadingState() {
        tvEstado.text = getString(R.string.order_tracking_loading)
        tvHoraLlegada.text = getString(R.string.order_tracking_loading)
        tvHoraEntrega.text = getString(R.string.order_tracking_loading)
        tvTrackingLastUpdate.text = getString(R.string.order_tracking_route_loading)
        pbProgreso.isIndeterminate = true
        pbProgreso.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) {
        pbProgreso.isIndeterminate = false
        pbProgreso.visibility = View.GONE
        tvEstado.text = message
        tvHoraLlegada.text = getString(R.string.order_tracking_error)
        tvHoraEntrega.text = getString(R.string.order_tracking_error)
        tvTrackingLastUpdate.text = getString(R.string.order_tracking_error)
        tvDireccion.text = getString(R.string.order_location_value)
        tvItemsTracking.text = getString(R.string.order_tracking_items_sample)
        tvTotal.text = toMxn(0.0)
    }

    private fun renderTracking(order: OrderDetailFullDto?, tracking: OrderTrackingDto?) {
        pbProgreso.isIndeterminate = false
        pbProgreso.visibility = View.VISIBLE

        val statusKey = (tracking?.status ?: order?.status).orEmpty().lowercase()
        val statusLabel = when (statusKey) {
            "pending", "preparing" -> getString(R.string.order_preparando)
            "ready" -> "Orden lista"
            "in_transit", "on_the_way", "delivering" -> "Pedido en camino"
            "completed", "delivered" -> "Pedido entregado"
            "canceled", "cancelled" -> "Pedido cancelado"
            else -> getString(R.string.order_preparando)
        }
        val etaText = when (statusKey) {
            "pending", "preparing" -> "15-30 min"
            "ready" -> "5-10 min"
            "in_transit", "on_the_way", "delivering" -> "En camino"
            "completed", "delivered" -> "Entregada"
            "canceled", "cancelled" -> "Cancelada"
            else -> "15-30 min"
        }

        tvEstado.text = if (orderId.isNotBlank()) {
            "$statusLabel · ${getString(R.string.order_tracking_order_number, orderId)}"
        } else {
            statusLabel
        }
        tvHoraLlegada.text = getString(R.string.order_llegando, etaText)
        tvHoraEntrega.text = getString(R.string.order_llegada_a_las, etaText)
        pbProgreso.progress = when (statusKey) {
            "pending", "preparing" -> 35
            "ready" -> 70
            "in_transit", "on_the_way", "delivering" -> 85
            "completed", "delivered" -> 100
            "canceled", "cancelled" -> 0
            else -> 35
        }

        val destinationText = tracking?.destination?.address
            ?: order?.address?.address
            ?: getString(R.string.order_tracking_address_sample)
        tvDireccion.text = destinationText

        val itemsText = if (!order?.details.isNullOrEmpty()) {
            order!!.details.joinToString("\n") { detail ->
                val productName = detail.product?.name ?: "Producto"
                "${detail.quantity} x $productName"
            }
        } else {
            getString(R.string.order_tracking_items_sample)
        }
        tvItemsTracking.text = itemsText

        tvTotal.text = toMxn(order?.total ?: 0.0)

        val updatedAt = tracking?.driverLocation?.updatedAt
            ?: tracking?.destination?.updatedAt
        tvTrackingLastUpdate.text = if (updatedAt.isNullOrBlank()) {
            getString(R.string.order_tracking_no_location)
        } else {
            getString(R.string.order_tracking_last_update, updatedAt)
        }

        renderTrackingOnMap()
    }

    private fun renderTrackingOnMap() {
        val map = googleMap ?: return
        val currentTracking = trackingInfo ?: return

        val destination = currentTracking.destination
        val driverLocation = currentTracking.driverLocation
        val destinationLat = destination?.lat
        val destinationLng = destination?.lng
        val driverLat = driverLocation?.lat
        val driverLng = driverLocation?.lng

        if (destinationLat == null || destinationLng == null) {
            return
        }

        // Si no hay repartidor asignado, mostrar overlay de "esperando"
        if (driverLat == null || driverLng == null) {
            mapWaitingOverlay.visibility = View.VISIBLE
            return
        }

        // Si hay repartidor, ocultar overlay y renderizar mapa
        mapWaitingOverlay.visibility = View.GONE

        routePolyline?.remove()
        driverMarker?.remove()
        destinationMarker?.remove()

        val destinationPoint = LatLng(destinationLat, destinationLng)

        destinationMarker = map.addMarker(
            MarkerOptions()
                .position(destinationPoint)
                .title(getString(R.string.order_tracking_destination_label))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        val driverPoint = LatLng(driverLat, driverLng)
        driverMarker = map.addMarker(
            MarkerOptions()
                .position(driverPoint)
                .title(getString(R.string.order_tracking_driver_label))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
        drawRoute(driverPoint, destinationPoint, map)

        val centerLat = (driverPoint.latitude + destinationPoint.latitude) / 2.0
        val centerLng = (driverPoint.longitude + destinationPoint.longitude) / 2.0
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(centerLat, centerLng), 13f))
    }

    private fun drawRoute(origen: LatLng, destino: LatLng, map: GoogleMap) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=${origen.latitude},${origen.longitude}" +
            "&destination=${destino.latitude},${destino.longitude}" +
            "&key=${getString(R.string.google_maps_api_key)}"

        val request = Request.Builder()
            .url(url)
            .build()

        httpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                runOnUiThread {
                    map.addPolyline(
                        PolylineOptions()
                            .add(origen, destino)
                            .color(Color.parseColor("#1F2937"))
                            .width(8f)
                    )
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    val polylinePoints = try {
                        val body = it.body?.string().orEmpty()
                        val jsonObject = JSONObject(body)
                        val routes = jsonObject.optJSONArray("routes")
                        val route = routes?.optJSONObject(0)
                        val points = route
                            ?.optJSONObject("overview_polyline")
                            ?.optString("points")

                        if (points.isNullOrBlank()) {
                            listOf(origen, destino)
                        } else {
                            decodePolyline(points)
                        }
                    } catch (_: Exception) {
                        listOf(origen, destino)
                    }

                    runOnUiThread {
                        routePolyline = map.addPolyline(
                            PolylineOptions()
                                .addAll(polylinePoints)
                                .color(Color.parseColor("#111827"))
                                .width(10f)
                        )
                    }
                }
            }
        })
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return poly
    }

    private fun toMxn(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        formatter.currency = java.util.Currency.getInstance("MXN")
        return formatter.format(value)
    }
}

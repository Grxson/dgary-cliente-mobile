package com.tuempresa.tuapp.ui.account.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.data.local.AddressStore
import com.tuempresa.tuapp.data.repository.AuthRepository
import com.tuempresa.tuapp.domain.model.SavedAddress
import com.tuempresa.tuapp.domain.model.AuthResult
import com.tuempresa.tuapp.ui.account.adapter.SavedAddressAdapter
import com.tuempresa.tuapp.ui.auth.view.LoginActivity
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvEditCuenta: LinearLayout
    private lateinit var btnAddAddress: LinearLayout
    private lateinit var rvSavedAddresses: RecyclerView
    private lateinit var btnCerrarSesion: LinearLayout
    private lateinit var addressAdapter: SavedAddressAdapter
    private lateinit var authRepository: AuthRepository
    
    private var savedAddresses = mutableListOf<SavedAddress>()
    private var editingAddressId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        authRepository = AuthRepository(this)

        initializeViews()
        setupListeners()
        loadUserData()
        loadSavedAddresses()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back_settings)
        tvUserName = findViewById(R.id.tv_settings_user_name)
        tvEditCuenta = findViewById(R.id.btn_edit_cuenta)
        btnAddAddress = findViewById(R.id.btn_add_address)
        rvSavedAddresses = findViewById(R.id.rv_saved_addresses)
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion)

        rvSavedAddresses.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        addressAdapter = SavedAddressAdapter(savedAddresses) { address, action ->
            handleAddressAction(address, action)
        }
        rvSavedAddresses.adapter = addressAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        tvEditCuenta.setOnClickListener {
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        btnAddAddress.setOnClickListener {
            startActivityForResult(
                Intent(this, AddressPickerActivity::class.java),
                REQUEST_CODE_ADD_ADDRESS
            )
        }

        btnCerrarSesion.setOnClickListener {
            lifecycleScope.launch {
                when (authRepository.logout()) {
                    is AuthResult.Success -> {
                        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    is AuthResult.Error -> {
                        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    AuthResult.Loading -> Unit
                }
            }
        }
    }

    private fun loadUserData() {
        // Datos de prueba
        tvUserName.text = getString(R.string.account_user_test)
    }

    private fun loadSavedAddresses() {
        savedAddresses.clear()
        savedAddresses.addAll(AddressStore.getAddresses(this))
        addressAdapter.notifyDataSetChanged()
    }

    private fun handleAddressAction(address: SavedAddress, action: String) {
        when (action) {
            SavedAddressAdapter.ACTION_EDIT -> {
                editingAddressId = address.id
                // Abrir AddressPickerActivity para editar
                startActivityForResult(
                    Intent(this, AddressPickerActivity::class.java).apply {
                        putExtra(AddressPickerActivity.EXTRA_ADDRESS_TITLE, address.titulo)
                        putExtra(AddressPickerActivity.EXTRA_ADDRESS_STRING, address.direccion)
                        putExtra(AddressPickerActivity.EXTRA_ADDRESS_LATITUDE, address.latitude ?: 0.0)
                        putExtra(AddressPickerActivity.EXTRA_ADDRESS_LONGITUDE, address.longitude ?: 0.0)
                    },
                    REQUEST_CODE_EDIT_ADDRESS
                )
            }
            SavedAddressAdapter.ACTION_DELETE -> {
                savedAddresses.remove(address)
                if (savedAddresses.none { it.esPrincipal } && savedAddresses.isNotEmpty()) {
                    savedAddresses[0] = savedAddresses[0].copy(esPrincipal = true)
                }
                AddressStore.saveAddresses(this, savedAddresses)
                addressAdapter.notifyDataSetChanged()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val title = data.getStringExtra(AddressPickerActivity.EXTRA_ADDRESS_TITLE) ?: return
            val lat = data.getDoubleExtra(AddressPickerActivity.EXTRA_ADDRESS_LATITUDE, 0.0)
            val lng = data.getDoubleExtra(AddressPickerActivity.EXTRA_ADDRESS_LONGITUDE, 0.0)
            val addressStr = data.getStringExtra(AddressPickerActivity.EXTRA_ADDRESS_STRING) ?: return

            when (requestCode) {
                REQUEST_CODE_ADD_ADDRESS -> {
                    // Crear nueva dirección
                    val newAddress = SavedAddress(
                        id = "${System.currentTimeMillis()}",
                        titulo = title,
                        direccion = addressStr,
                        detalles = "Lat: $lat, Lng: $lng",
                        esPrincipal = savedAddresses.isEmpty(),
                        latitude = lat,
                        longitude = lng
                    )
                    savedAddresses.add(newAddress)
                    AddressStore.saveAddresses(this, savedAddresses)
                }
                REQUEST_CODE_EDIT_ADDRESS -> {
                    // Actualizar dirección existente
                    val index = savedAddresses.indexOfFirst { it.id == editingAddressId }
                    if (index != -1) {
                        savedAddresses[index] = savedAddresses[index].copy(
                            titulo = title,
                            direccion = addressStr,
                            detalles = "Lat: $lat, Lng: $lng",
                            latitude = lat,
                            longitude = lng
                        )
                        AddressStore.saveAddresses(this, savedAddresses)
                    }
                    editingAddressId = null
                }
            }
            addressAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_ADDRESS = 100
        private const val REQUEST_CODE_EDIT_ADDRESS = 101
    }
}


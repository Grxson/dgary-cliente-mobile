package com.tuempresa.tuapp.ui.order.view

import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.tuapp.R

class LocationSelectionActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnActivateLocation: MaterialButton
    private lateinit var btnEditLocation1: ImageView
    private lateinit var btnEditLocation2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_selection)

        btnBack = findViewById(R.id.btn_back_location)
        btnActivateLocation = findViewById(R.id.btn_activate_location)
        btnEditLocation1 = findViewById(R.id.btn_edit_location_1)
        btnEditLocation2 = findViewById(R.id.btn_edit_location_2)

        btnBack.setOnClickListener { finish() }
        btnActivateLocation.setOnClickListener { finish() }
        btnEditLocation1.setOnClickListener { finish() }
        btnEditLocation2.setOnClickListener { finish() }
    }
}


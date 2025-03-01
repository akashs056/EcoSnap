package com.example.ecosnap.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecosnap.R
import com.example.ecosnap.databinding.ActivityMapViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapView : AppCompatActivity() {
    private lateinit var binding: ActivityMapViewBinding
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapViewBinding.inflate(layoutInflater)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        latitude = intent.getDoubleExtra("latitude", 0.0)

        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Report Location)")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps") // Ensures Google Maps app opens

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Fallback: Open in browser if Google Maps app isn't installed
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
            )
            startActivity(browserIntent)
        }

        finish() // Close the activity after opening Maps

        setContentView(binding.root)
    }




}
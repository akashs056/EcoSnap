package com.example.ecosnap.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.ecosnap.R
import com.example.ecosnap.Utils.GlobalVariables
import com.example.ecosnap.Utils.GlobalVariables.isWorker
import com.example.ecosnap.databinding.ActivityMainBinding
import com.example.ecosnap.fragments.ExploreFragment
import com.example.ecosnap.fragments.Fragment2
import com.example.ecosnap.fragments.HomeFragment
import com.example.ecosnap.fragments.LeaderBoardFragment
import com.example.ecosnap.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var capturedImageUri: Uri? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        setContentView(binding.root)
        checkLocationPermission()
        forceLightTheme()
        setUserDetails()
        setUpBottomNavigation()
        setUpSnapButton()

        loadFragment(HomeFragment())
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserDetails() {
        GlobalVariables.name = sharedPreferences.getString("name", "").toString()
        GlobalVariables.email = sharedPreferences.getString("email", "").toString()
        GlobalVariables.isWorker = sharedPreferences.getBoolean("isWorker", false)
        if (isWorker){
            binding.snap.visibility = View.GONE
        }else{
            binding.snap.visibility = View.VISIBLE
        }
    }

    private fun setUpSnapButton(){
        binding.snap.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.CAMERA] == true
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Redirect user to FormSubmissionActivity with the captured image URI
                val intent = Intent(this, FormSubmissionActvity::class.java).apply {
                    putExtra("captured_image_uri", capturedImageUri.toString())
                }
                startActivity(intent)
            }
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image.jpg")
        capturedImageUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        cameraLauncher.launch(intent)
    }

    private fun setUpBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(ExploreFragment())
                    true
                }
                R.id.leaderboard -> {
                    loadFragment(LeaderBoardFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragments,fragment)
        transaction.commit()
    }
    private fun forceLightTheme() {
        if (this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
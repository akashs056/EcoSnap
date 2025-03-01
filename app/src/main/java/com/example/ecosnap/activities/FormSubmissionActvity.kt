package com.example.ecosnap.activities

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecosnap.R
import com.example.ecosnap.Utils.GlobalVariables
import com.example.ecosnap.databinding.ActivityFormSubmissionActvityBinding
import com.example.ecosnap.viewmodels.MainViewmodel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage

class FormSubmissionActvity : AppCompatActivity() {
    private  val REQUEST_CHECK_SETTINGS= 151
    private  val REQUEST_LOCATION_PERMISSION = 1001
    private lateinit var binding: ActivityFormSubmissionActvityBinding
    private var selectedMaterial: String = "Dry"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var imageUri: Uri? = null
    private lateinit var viewmodel: MainViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormSubmissionActvityBinding.inflate(layoutInflater)
        viewmodel = MainViewmodel(application)
        setContentView(binding.root)
        requestCurrentLocation()
        forceLightTheme()
        handleIncomingImage()
        setUpWasteTypeDropDown()
        viewmodel.isLoading.observe(this){
            if(it) showProgressBar() else hideProgressBar()
        }
        viewmodel.isReportWastePosted.observe(this){
            it.onSuccess {
                Toast.makeText(this, "Report Posted Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.submitButton.setOnClickListener {
            if (binding.descriptionEditText.text.isEmpty()){
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpWasteTypeDropDown() {
        val items = arrayOf("Dry", "Wet", "Combined", "E-Waste")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item, items)

        adapter.setDropDownViewResource(R.layout.simple_list_item)
        // Apply the adapter to the spinner
        binding.wasteTypeSelector.adapter = adapter
        // Set the listener for when an item is selected
        binding.wasteTypeSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Store the selected item in the variable
                selectedMaterial = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedMaterial = "Dry"
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
        viewmodel.setLoading(true)
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    viewmodel.postReportWaste(GlobalVariables.email,downloadUrl,selectedMaterial,binding.descriptionEditText.text.toString(),"$latitude $longitude")
                }
            }
            .addOnFailureListener {
                viewmodel.setLoading(false)
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleIncomingImage() {
        val uriString = intent.getStringExtra("captured_image_uri")
        uriString?.let {
            imageUri = Uri.parse(it)
            binding.wasteImg.setImageURI(imageUri) // Show the captured image
        } ?: run {
            Toast.makeText(this, "No image found!", Toast.LENGTH_SHORT).show()
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private var cancellationTokenSource = CancellationTokenSource()


    private fun requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            // Build a LocationSettingsRequest to check if location settings are satisfied
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client: SettingsClient =
                LocationServices.getSettingsClient(this@FormSubmissionActvity)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            // Check if location settings are satisfied
            task.addOnSuccessListener { locationSettingsResponse ->
                // Location settings are satisfied, proceed to get current location
                val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token
                )
                // Get current location
                currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                    if (task.isSuccessful && task.result != null) {
                        // Location retrieval successful, update UI with location details
                        val result: Location = task.result
                        Log.d(
                            "EcoSnapDebug",
                            "Location (success): ${result.latitude}, ${result.longitude}"
                        )
                        longitude = result.longitude
                        latitude = result.latitude

                    } else {
                        // Location retrieval failed, log error
                        val exception = task.exception
                        Log.d("EcoSnapDebug", "Location (error): ${exception?.message}")
                    }
                }
            }
            // Handle failure to satisfy location settings
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, show a dialog to prompt the user to change settings
                    try {
                        exception.startResolutionForResult(
                            this, REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Error starting resolution, ignore
                    }
                } else {

                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }




    private fun showProgressBar(){
        binding.submitButton.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.submitButton.visibility = View.VISIBLE
    }
    private fun forceLightTheme() {
        if (this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
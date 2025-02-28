package com.example.ecosnap.activities

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ecosnap.R
import com.example.ecosnap.Utils.GlobalVariables
import com.example.ecosnap.databinding.ActivityFormSubmissionActvityBinding
import com.example.ecosnap.viewmodels.MainViewmodel
import com.google.firebase.storage.FirebaseStorage

class FormSubmissionActvity : AppCompatActivity() {
    private lateinit var binding: ActivityFormSubmissionActvityBinding
    private var selectedMaterial: String = "Dry"
    private var imageUri: Uri? = null
    private lateinit var viewmodel: MainViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormSubmissionActvityBinding.inflate(layoutInflater)
        viewmodel = MainViewmodel(application)
        setContentView(binding.root)
        forceLightTheme()
        handleIncomingImage()
        setUpWasteTypeDropDown()
        viewmodel.isLoading.observe(this){
            if(it) showProgressBar() else hideProgressBar()
        }
        viewmodel.isReportWastePosted.observe(this){
            it.onSuccess {
                Toast.makeText(this, "Report Posted Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.submitButton.setOnClickListener {
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpWasteTypeDropDown() {
        val items = arrayOf("Dry", "Wet", "E-Waste")
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
                    viewmodel.postReportWaste(GlobalVariables.email,downloadUrl,selectedMaterial,binding.descriptionEditText.text.toString())
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
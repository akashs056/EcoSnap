package com.example.ecosnap.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecosnap.R
import com.example.ecosnap.Utils.GlobalVariables
import com.example.ecosnap.Utils.GlobalVariables.email
import com.example.ecosnap.activities.MapView
import com.example.ecosnap.activities.MapsActivity
import com.example.ecosnap.adapters.RequestAdapter
import com.example.ecosnap.databinding.FragmentHomeBinding
import com.example.ecosnap.viewmodels.MainViewmodel
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewmodel
    private lateinit var requestAdapter: RequestAdapter
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var capturedImageUri: Uri? = null
    private var clickedReportId : String ? = null
    private var alertDialog : AlertDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewmodel::class.java]
        requestAdapter = RequestAdapter(requireContext(), emptyList(), onItemClick = {
            val location = it.location
            val latitude = location.split(" ")[0].toDouble()
            val longitude = location.split(" ")[1].toDouble()
            val intent = Intent(requireContext(), MapView::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivity(intent)
        }, onCompleteClick = {
            clickedReportId = it._id
            checkCameraPermissionAndOpenCamera()
        }
            )
        binding.requestRv.layoutManager = LinearLayoutManager(requireContext())
        binding.requestRv.adapter = requestAdapter
        viewModel.fetchWasteReports(GlobalVariables.email)
        viewModel.fetchUserDetails(GlobalVariables.email)
        viewModel.wasteReports.observe(viewLifecycleOwner){reports ->
            requestAdapter.updateList(reports.getOrNull()?.reversed() ?: emptyList())
        }
        viewModel.isCleanReportWastePosted.observe(viewLifecycleOwner){
            it.onSuccess {
                Toast.makeText(requireContext(), "Report Posted Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragments,HomeFragment())
                    .commit()
                hideDailog()
            }.onFailure { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                hideDailog()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner){
            if (it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.fetchUserDetail.observe(viewLifecycleOwner){
            binding.points.text = it.getOrNull()?.points.toString()
        }

        binding.ecoWallet.setOnClickListener {
            val fragment = EcoWaletFragment()
            val bundle = Bundle()
            bundle.putString("points", binding.points.text.toString())
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragments, fragment)
                .addToBackStack(null)
                .commit()

        }

        binding.impact.setOnClickListener {
            val fragment = ImpactFragment()
            replaceFragments(fragment)
        }

        binding.explore.setOnClickListener {
            val fragment = ExploreFragment()
            replaceFragments(fragment)
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.CAMERA] == true
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                showLoadingDialog(requireContext())
                uploadImageToFirebase(capturedImageUri!!)
            }
        }


        return binding.root
    }

    private fun uploadImageToFirebase(capturedImageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(capturedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    viewModel.postCleanImage(clickedReportId,email,downloadUrl)
                }
            }
            .addOnFailureListener {
                hideDailog()
                Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image.jpg")
        capturedImageUri = FileProvider.getUriForFile(requireContext(), "${requireActivity().packageName}.provider", imageFile)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        cameraLauncher.launch(intent)
    }

    fun showLoadingDialog(context: Context?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(com.example.ecosnap.R.layout.alert_dialog, null)
        builder.setView(view)
        builder.setCancelable(false) // Prevent dismissing by tapping outside
        val dialog: AlertDialog = builder.create()
        alertDialog = dialog
        dialog.show()
    }

    fun hideDailog(){
        alertDialog?.hide()
    }


    private fun replaceFragments(fragment: Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragments, fragment)
            .addToBackStack(null)
            .commit()

    }

}
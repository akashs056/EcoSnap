package com.example.ecosnap.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ecosnap.activities.MainActivity
import com.example.ecosnap.R
import com.example.ecosnap.Utils.AppConstants.isInternetConnected
import com.example.ecosnap.Utils.AppConstants.showToast
import com.example.ecosnap.auth.viewmodel.AuthViewModel
import com.example.ecosnap.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AuthViewModel
    private var email: String = ""
    private var password: String = ""
    private var name: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        viewModel = AuthViewModel(requireActivity().application)

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it) showProgressBar() else hideProgressBar()
        }

        viewModel.authResult.observe(viewLifecycleOwner) { authResult ->
            authResult.onSuccess {
                navigateToMainActivity()
                showToast(requireContext(),"Account Created Successfully")
            }.onFailure { error ->
                showToast(requireContext(), error.message ?: "Authentication Failed")
            }
        }

        viewModel.userSavedInBackend.observe(viewLifecycleOwner){
            it.onSuccess {
                viewModel.signUpWithEmail(name,email,password)
            }.onFailure { error ->
                showToast(requireContext(), error.message ?: "Authentication Failed")
            }
        }

        binding.backBtn.setOnClickListener {
            val authFragment = AuthFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,authFragment).commit()
        }
        binding.login.setOnClickListener {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,loginFragment).commit()
        }

        binding.signUpButton.setOnClickListener {
            if (!isInternetConnected(requireContext())){
                showToast(requireContext(), "No internet connection")
                return@setOnClickListener
            }
            email = binding.emailEd.text.toString().trim()
            password = binding.passwordEd.text.toString().trim()
            name = binding.nameEd.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.saveUserToBackend(name, email, password)
            } else {
                showToast(requireContext(), "Please enter email and password")
            }
        }

        return binding.root
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showProgressBar(){
        binding.signUpButton.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.signUpButton.visibility = View.VISIBLE
    }

}
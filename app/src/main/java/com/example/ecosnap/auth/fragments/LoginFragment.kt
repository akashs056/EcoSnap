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
import com.example.ecosnap.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AuthViewModel
    private var email: String = ""
    private var password: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()
        viewModel = AuthViewModel(requireActivity().application)

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it) showProgressBar() else hideProgressBar()
        }

        viewModel.authResult.observe(viewLifecycleOwner) { authResult ->
            authResult.onSuccess {
                navigateToMainActivity()
                showToast(requireContext(),"Login Successful")
            }.onFailure { error ->
                showToast(requireContext(), error.message ?: "Authentication Failed")
            }
        }

        binding.backBtn.setOnClickListener {
            val authFragment = AuthFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,authFragment).commit()
        }
        binding.signUp.setOnClickListener {
            val signUpFragment = SignUpFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,signUpFragment).commit()
        }

        binding.logInButton.setOnClickListener {
            if (!isInternetConnected(requireContext())){
                showToast(requireContext(), "No internet connection")
                return@setOnClickListener
            }
            email = binding.emailEd.text.toString().trim()
            password = binding.passwordEd.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signInWithEmail(email,password)
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
        binding.logInButton.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.logInButton.visibility = View.VISIBLE
    }


}
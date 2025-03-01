package com.example.ecosnap.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ecosnap.R
import com.example.ecosnap.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.login.setOnClickListener {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,loginFragment).commit()
        }
        binding.createAccount.setOnClickListener {
            val signUpFragment = SignUpFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,signUpFragment).commit()
        }
        binding.loginAsWorker.setOnClickListener {
            val workerLoginFragment = WorkerLoginFragment()
            parentFragmentManager.beginTransaction().replace(R.id.auth_fragments,workerLoginFragment).commit()
        }
        return binding.root
    }

}
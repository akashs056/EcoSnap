package com.example.ecosnap.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.ecosnap.R
import com.example.ecosnap.Utils.GlobalVariables
import com.example.ecosnap.auth.LaunchActivity
import com.example.ecosnap.databinding.FragmentProfileBinding
import com.example.ecosnap.viewmodels.MainViewmodel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: MainViewmodel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(MainViewmodel::class.java)
        binding.email.text = GlobalVariables.email
        binding.name.text = GlobalVariables.name

        binding.logout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LaunchActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }
}
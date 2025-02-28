package com.example.ecosnap.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecosnap.Utils.GlobalVariables
import com.example.ecosnap.adapters.RequestAdapter
import com.example.ecosnap.auth.LaunchActivity
import com.example.ecosnap.databinding.FragmentHomeBinding
import com.example.ecosnap.viewmodels.MainViewmodel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewmodel
    private lateinit var requestAdapter: RequestAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewmodel::class.java]
        requestAdapter = RequestAdapter(requireContext(), emptyList())
        binding.requestRv.layoutManager = LinearLayoutManager(requireContext())
        binding.requestRv.adapter = requestAdapter
        viewModel.fetchWasteReports(GlobalVariables.email)
        viewModel.fetchUserDetails(GlobalVariables.email)
        viewModel.wasteReports.observe(viewLifecycleOwner){reports ->
            requestAdapter.updateList(reports.getOrNull()?.reversed() ?: emptyList())
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

        return binding.root
    }

}
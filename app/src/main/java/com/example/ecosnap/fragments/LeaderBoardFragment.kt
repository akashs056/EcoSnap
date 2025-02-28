package com.example.ecosnap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecosnap.R
import com.example.ecosnap.adapters.LeaderboardAdapter
import com.example.ecosnap.adapters.RequestAdapter
import com.example.ecosnap.databinding.FragmentLeaderBoardBinding
import com.example.ecosnap.viewmodels.MainViewmodel

class LeaderBoardFragment : Fragment() {
    private lateinit var binding: FragmentLeaderBoardBinding
    private lateinit var viewmodel: MainViewmodel
    private lateinit var adapter: LeaderboardAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderBoardBinding.inflate(layoutInflater,container,false)
        viewmodel = ViewModelProvider(this).get(MainViewmodel::class.java)
        viewmodel.fetchLeaderboard()
        adapter = LeaderboardAdapter(requireContext(), emptyList())
        binding.leaderboardRv.layoutManager = LinearLayoutManager(requireContext())
        binding.leaderboardRv.adapter = adapter
        viewmodel.fetchLeaderboard.observe(viewLifecycleOwner){
            adapter.updateList(it.getOrNull() ?: emptyList())
        }
        viewmodel.isLoading.observe(viewLifecycleOwner){
            if (it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }

        return binding.root
    }

}
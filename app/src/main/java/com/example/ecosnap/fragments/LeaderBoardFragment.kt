package com.example.ecosnap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ecosnap.R
import com.example.ecosnap.databinding.FragmentLeaderBoardBinding

class LeaderBoardFragment : Fragment() {
    private lateinit var binding: FragmentLeaderBoardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderBoardBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}
package com.example.ecosnap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ecosnap.R
import com.example.ecosnap.databinding.FragmentEcoWaletBinding

class EcoWaletFragment : Fragment() {
    private lateinit var binding: FragmentEcoWaletBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEcoWaletBinding.inflate(inflater, container, false)
        val points = arguments?.getString("points")
        binding.points.text = points
        return binding.root
    }

}
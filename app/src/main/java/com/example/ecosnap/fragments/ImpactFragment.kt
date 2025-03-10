package com.example.ecosnap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecosnap.databinding.FragmentImpactBinding


class ImpactFragment : Fragment() {

    private lateinit var binding: FragmentImpactBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImpactBinding.inflate(inflater, container, false)
        return binding.root
    }
}
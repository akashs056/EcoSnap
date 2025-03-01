package com.example.ecosnap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecosnap.R
import com.example.ecosnap.adapters.EventAdapter
import com.example.ecosnap.databinding.FragmentExploreBinding
import com.example.ecosnap.viewmodels.MainViewmodel

class ExploreFragment : Fragment() {
    private lateinit var binding: FragmentExploreBinding
    private lateinit var viewmodel: MainViewmodel
    private lateinit var adapter: EventAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(this)[MainViewmodel::class.java]
        adapter = EventAdapter(requireContext(), emptyList(), onItemClick = {
        })
        binding.eventsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsRv.adapter = adapter
        viewmodel.fetchAllEvents()
        viewmodel.isLoading.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.fetchAllEvents.observe(viewLifecycleOwner) {
            adapter.updateList(it.getOrNull() ?: emptyList())
        }
        return binding.root
    }
}
package com.example.ecosnap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecosnap.R
import com.example.ecosnap.databinding.SampleEventCardBinding
import com.example.ecosnap.databinding.SampleRequestBinding
import com.example.ecosnap.models.Event
import com.example.ecosnap.models.WasteReportCard

class EventAdapter(
    private val context: Context,
    private var itemList: List<Event>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapterViewHolder {
        val binding = SampleEventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventAdapterViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item,onItemClick)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(newList: List<Event>) {
        itemList = newList
        notifyDataSetChanged() // Notify adapter to refresh the data
    }

    class EventAdapterViewHolder(private val binding: SampleEventCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: Event, onItemClick: (Event) -> Unit) {
            binding.title.text = item.title
            binding.description.text = item.description
            binding.date.text = item.date.take(10)
            binding.time.text = item.time
            binding.points.text = item.points.toString() +" points"
            binding.participants.text = "10+"
            binding.locaion.text = item.location

            binding.actionButton.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
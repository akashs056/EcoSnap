package com.example.ecosnap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecosnap.R
import com.example.ecosnap.databinding.SampleRequestBinding
import com.example.ecosnap.models.WasteReportCard

class RequestAdapter(
    private val context: Context,
    private var itemList: List<WasteReportCard>
) : RecyclerView.Adapter<RequestAdapter.RequestAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestAdapterViewHolder {
        val binding = SampleRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestAdapterViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(newList: List<WasteReportCard>) {
        itemList = newList
        notifyDataSetChanged() // Notify adapter to refresh the data
    }

    class RequestAdapterViewHolder(private val binding: SampleRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: WasteReportCard) {
            binding.type.text = item.type
            binding.description.text = item.description
            binding.createdAt.text = item.createdAt
            binding.status.text = item.status
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.wasteImg)
        }
    }
}

package com.example.ecosnap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecosnap.R
import com.example.ecosnap.Utils.GlobalVariables.isWorker
import com.example.ecosnap.databinding.SampleRequestBinding
import com.example.ecosnap.models.WasteReportCard

class RequestAdapter(
    private val context: Context,
    private var itemList: List<WasteReportCard>,
    private val onItemClick: (WasteReportCard) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestAdapterViewHolder {
        val binding = SampleRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestAdapterViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item, onItemClick)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(newList: List<WasteReportCard>) {
        itemList = newList
        notifyDataSetChanged() // Notify adapter to refresh the data
    }

    class RequestAdapterViewHolder(private val binding: SampleRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: WasteReportCard, onItemClick: (WasteReportCard) -> Unit) {
            binding.type.text = item.type
            binding.description.text = item.description
            binding.createdAt.text = item.createdAt
            binding.status.text = item.status
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.wasteImg)
            if (isWorker){
                if (item.status == "Completed"){
                    binding.complete.visibility = ViewGroup.VISIBLE
                    binding.complete.setImageResource(R.drawable.completed)
                }else if (item.status == "Rejected"){
                    binding.complete.visibility = ViewGroup.INVISIBLE
                }
                else {
                    binding.complete.visibility = ViewGroup.VISIBLE
                    binding.complete.setImageResource(R.drawable.to_complete)
                }
            }else{
                binding.complete.visibility = ViewGroup.INVISIBLE
            }
            binding.complete.setOnClickListener {
                if (item.status != "Completed" && item.status != "Rejected") {
                    onItemClick(item)
                }
            }
        }
    }
}

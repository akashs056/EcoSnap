package com.example.ecosnap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecosnap.databinding.SampleLeaderboardCardBinding
import com.example.ecosnap.models.LeaderboardCard

class LeaderboardAdapter(
    private val context: Context,
    private var itemList: List<LeaderboardCard>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardAdapterViewHolder {
        val binding = SampleLeaderboardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardAdapterViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item)
    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(newList: List<LeaderboardCard>) {
        itemList = newList
        notifyDataSetChanged() // Notify adapter to refresh the data
    }

    class LeaderboardAdapterViewHolder(private val binding: SampleLeaderboardCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: LeaderboardCard) {
            binding.rank.text = item.rank.toString()
            binding.name.text = item.name
            binding.points.text = item.points.toString()
        }
    }
}

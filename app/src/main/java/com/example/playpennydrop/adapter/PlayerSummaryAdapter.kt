package com.example.playpennydrop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.playpennydrop.R
import com.example.playpennydrop.databinding.PlayerSummaryListItemBinding
import com.example.playpennydrop.types.PlayerSummary

class PlayerSummaryAdapter : ListAdapter<PlayerSummary, PlayerSummaryAdapter.PlayerSummaryViewHolder>(
    PlayerSummaryDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerSummaryViewHolder {
        return PlayerSummaryViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.player_summary_list_item,
                parent,
                false
            ),
        )
    }

    override fun onBindViewHolder(holder: PlayerSummaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlayerSummaryViewHolder(
        private val binding: PlayerSummaryListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlayerSummary) {
            binding.apply {
                playerSummary = item
                executePendingBindings()
            }
        }
    }
}

private class PlayerSummaryDiffCallback : DiffUtil.ItemCallback<PlayerSummary>() {
    override fun areItemsTheSame(oldItem: PlayerSummary, newItem: PlayerSummary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlayerSummary, newItem: PlayerSummary): Boolean {
        return oldItem == newItem
    }

}
package io.github.dev_connor.maxzenith

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.dev_connor.maxzenith.data.Youtube
import io.github.dev_connor.maxzenith.databinding.ItemYoutubeBinding
import io.github.dev_connor.maxzenith.YoutubeAdapter.YoutubeItemViewHolder

class YoutubeAdapter: ListAdapter<Youtube, YoutubeItemViewHolder>(diffUtil) {
    inner class YoutubeItemViewHolder(private val binding: ItemYoutubeBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(youtube: Youtube) {
            binding.textviewYoutubeUser.text = youtube.user
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeItemViewHolder {
        return YoutubeItemViewHolder(ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: YoutubeItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Youtube>() {
            override fun areItemsTheSame(oldItem: Youtube, newItem: Youtube): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Youtube, newItem: Youtube): Boolean {
                return oldItem.user == newItem.user
            }

        }
    }

}
package io.github.dev_connor.maxzenith

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.dev_connor.maxzenith.data.YoutubeTest
import io.github.dev_connor.maxzenith.databinding.ItemYoutubeBinding
import io.github.dev_connor.maxzenith.YoutubeAdapter.YoutubeItemViewHolder

class YoutubeAdapter: ListAdapter<YoutubeTest, YoutubeItemViewHolder>(diffUtil) {
    inner class YoutubeItemViewHolder(private val binding: ItemYoutubeBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(youtube: YoutubeTest) {
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
        val diffUtil = object : DiffUtil.ItemCallback<YoutubeTest>() {
            override fun areItemsTheSame(oldItem: YoutubeTest, newItem: YoutubeTest): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: YoutubeTest, newItem: YoutubeTest): Boolean {
                return oldItem.user == newItem.user
            }

        }
    }

}
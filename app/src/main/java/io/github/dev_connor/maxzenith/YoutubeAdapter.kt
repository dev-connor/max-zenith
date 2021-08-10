package io.github.dev_connor.maxzenith

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.dev_connor.maxzenith.data.Item
import io.github.dev_connor.maxzenith.YoutubeAdapter.YoutubeItemViewHolder
import io.github.dev_connor.maxzenith.databinding.ItemYoutubeBinding

class YoutubeAdapter: ListAdapter<Item, YoutubeItemViewHolder>(diffUtil) {
    inner class YoutubeItemViewHolder(private val binding: ItemYoutubeBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.textviewYoutubeTitle.text = item.snippet.title
            binding.textviewYoutubeDescription.text = item.snippet.description
            binding.textviewYoutubeChannel.text = item.snippet.channelTitle

            /* 글라이드: 이미지 URL 라이브러리 */
            Glide.with(binding.imageviewYoutubeVideo.context)
                .load(item.snippet.thumbnails.maxres.url)
                .into(binding.imageviewYoutubeVideo)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeItemViewHolder {
        return YoutubeItemViewHolder(ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: YoutubeItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.snippet.thumbnails.maxres.url == newItem.snippet.thumbnails.maxres.url
            }

        }
    }

}
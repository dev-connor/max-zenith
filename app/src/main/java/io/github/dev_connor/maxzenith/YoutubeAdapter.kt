package io.github.dev_connor.maxzenith

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.dev_connor.maxzenith.data.Item
import io.github.dev_connor.maxzenith.YoutubeAdapter.YoutubeItemViewHolder
import io.github.dev_connor.maxzenith.data.Video
import io.github.dev_connor.maxzenith.databinding.ItemYoutubeBinding

class YoutubeAdapter: ListAdapter<Video, YoutubeItemViewHolder>(diffUtil) {
    inner class YoutubeItemViewHolder(private val binding: ItemYoutubeBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.textviewYoutubeTitle.text = video.title
            binding.textviewYoutubeChannel.text = video.channelTitle

            /* 글라이드: 이미지 URL 라이브러리 */
            Glide.with(binding.imageviewYoutubeVideo.context)
                .load(video.url)
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
        val diffUtil = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldVideo: Video, newVideo: Video): Boolean {
                return oldVideo.url == newVideo.url
            }
        }
    }
}
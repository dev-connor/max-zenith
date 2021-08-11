package io.github.dev_connor.maxzenith

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.dev_connor.maxzenith.YoutubeAdapter.YoutubeItemViewHolder
import io.github.dev_connor.maxzenith.data.Video
import io.github.dev_connor.maxzenith.databinding.ItemYoutubeBinding
import java.lang.Exception

class YoutubeAdapter: ListAdapter<Video, YoutubeItemViewHolder>(diffUtil) {
    inner class YoutubeItemViewHolder(private val binding: ItemYoutubeBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.textviewYoutubeTitle.text = video.title
            binding.textviewYoutubeChannel.text = video.channelTitle
            binding.textviewYoutubeLike.text = "좋아요 n개"
            binding.textviewYoutubeDescription.text = "내용"
            binding.textviewYoutubeComment.text = "댓글"


            /* 유튜브연결 버튼 */
            binding.imageviewYoutubeVideo.setOnClickListener {
                val youtubeURL = ""
                Toast.makeText(binding.imageviewYoutubeVideo.context,
                    "유튜브를 연결할 수 없습니다.",
                    Toast.LENGTH_SHORT).show()
                if (youtubeURL != null) {
//                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL))
//                    startActivity(intent)
                }
            }

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
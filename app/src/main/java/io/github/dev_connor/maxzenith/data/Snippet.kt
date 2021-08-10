package io.github.dev_connor.maxzenith.data

import com.google.gson.annotations.SerializedName

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val channelId: String
)

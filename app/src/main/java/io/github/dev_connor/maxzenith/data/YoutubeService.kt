package io.github.dev_connor.maxzenith.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeService {
    @GET("/youtube/v3/playlists?part=id,snippet&maxResults=3&key=AIzaSyBpiYuuOnCz7aKgBZEaldurIH8wfix7i88")
    fun getPlaylists(
        @Query("id") id: String
    ): Call<Youtube>
}
package io.github.dev_connor.maxzenith.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeService {
    @GET("/youtube/v3/playlists?part=id,snippet&id=PLQAswsu-FWprVUEyOlzbRLPdapZr4Fc98&maxResults=3")
    fun getPlaylists(
        @Query("key") apiKey: String
    ): Call<Youtube>
}
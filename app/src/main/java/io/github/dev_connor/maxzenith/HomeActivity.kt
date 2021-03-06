package io.github.dev_connor.maxzenith

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.dev_connor.maxzenith.data.Video
import io.github.dev_connor.maxzenith.data.Youtube
import io.github.dev_connor.maxzenith.data.YoutubeService
import io.github.dev_connor.maxzenith.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class HomeActivity : AppCompatActivity() {
    private lateinit var imgProfile: ImageView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: YoutubeAdapter
    private lateinit var binding: ActivityHomeBinding
    private val videos = mutableListOf<Video>()
    private var haveURL = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        adapter = YoutubeAdapter()
        setContentView(binding.root)
        binding.recyclerViewHomeYoutube.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomeYoutube.adapter = adapter

        auth = Firebase.auth




        /* ??????????????? ?????? */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        /* findViewById */
        imgProfile = findViewById<ImageView>(R.id.imageView_home_profile)
        val imgPost = findViewById<ImageView>(R.id.iamgeView_home_post)
        val textURL = findViewById<TextView>(R.id.textView_home_url)

        /* ?????? */
        /* ?????????????????? ?????? */
//        val imgDelete = findViewById<ImageView>(R.id.imageView_home_delete)
//        imgDelete.setOnClickListener {
//            editId.setText("")
//        }

        /* ??????????????? ??? ?????? */
        imgProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        /* ????????????: API ??????????????? */
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val youtubeService = retrofit.create(YoutubeService::class.java)

        /* ???????????? ???????????? ?????? */
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        imgPost.setOnClickListener {
            if (haveURL == false) {
                if (!clipboard.hasPrimaryClip()) {
                    Toast.makeText(this, "????????? URL ??? ????????????.", Toast.LENGTH_SHORT).show()
                }
                val pasteData = clipboard.primaryClip?.getItemAt(0)?.text as String
                if (pasteData.contains("list=")) {
                    Toast.makeText(this, "???????????? ?????????.", Toast.LENGTH_SHORT).show()
                    Log.e("haveURL", haveURL.toString() + "-> true")
                    haveURL = true
                } else if (pasteData.contains("v=")) {
                    Toast.makeText(this, "???????????? ?????????.", Toast.LENGTH_SHORT).show()
                    Log.e("haveURL", haveURL.toString() + "-> true")
                    haveURL = true
                } else if (pasteData.contains("youtu.be/")) {
                    Toast.makeText(this, "???????????? ?????????.", Toast.LENGTH_SHORT).show()
                    Log.e("haveURL", haveURL.toString() + "-> true")
                    haveURL = true
                } else {
                    Toast.makeText(this, "????????? URL ?????????.", Toast.LENGTH_SHORT).show()
                }
                textURL.setText(pasteData)
            } else {

                /* ???????????????????????? ?????? */
                val youtubeURL = textURL.text.toString()
                var videoId = ""

                /* URL ??? ??????????????? ?????? */
                if (youtubeURL.contains("list=")) {
                    try {
                        videoId = youtubeURL.substring(youtubeURL.indexOf("list=") + 5)
                    } catch (e: Exception) {
                        Toast.makeText(this, "URL ??? ?????? ????????????.", Toast.LENGTH_SHORT).show()
                    }
                    youtubeService.getPlaylists(videoId)
                        .enqueue(object : Callback<Youtube> {
                            override fun onResponse(
                                call: Call<Youtube>,
                                response: Response<Youtube>
                            ) {
                                if (response.isSuccessful.not()) {
                                    Log.e("TAG", "Not!! Success")
                                    return
                                }
                                response.body()?.let {
                                    it.items.forEach {
                                        val video = it.snippet
                                        try {
                                            saveVideoInfo(videoId, video.channelTitle,
                                                video.title,
                                                video.thumbnails.maxres.url, youtubeURL)
                                            textURL.setText("")
                                            haveURL = false
                                        } catch (e: Exception) {
                                            Log.e("Youtube API", "???????????? ???????????? ???????????? ??????????????? ??????????????????.")
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Youtube>, t: Throwable) {
                                Log.e("Youtube API", "????????? API ??? ??????????????? ??????????????????.")
                            }
                        })

                    /* URL ??? ??????????????? ?????? ?????? */
                } else if (youtubeURL.contains("v=")){
                    val test = youtubeURL
                    val startIndex = test.indexOf("v=")
                    if (test.contains('&')) {
                        val endIndex = test.indexOf('&')
                        videoId = test.substring(startIndex + 2, endIndex)
                    } else {
                        videoId = test.substring(startIndex + 2)
                    }
                    val imageLink = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg"
                    try {
                        Log.e("v=", videoId + ", " + imageLink + ", " + test)
                        saveVideoInfo(videoId, "?????????",
                            "??????",
                            imageLink, test)
                        textURL.setText("")
                        haveURL = false
                    } catch (e: Exception) {
                        Log.e("Youtube API", "???????????? ???????????? ???????????? ??????????????? ??????????????????.")
                    }
                } else if (youtubeURL.contains("youtu.be/")){
                    val test = youtubeURL
                    val startIndex = test.indexOf("youtu.be/")
                    videoId = test.substring(startIndex + 9)
                    val imageLink = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg"
                    try {
                        saveVideoInfo(videoId, "?????????",
                            "??????",
                            imageLink, test)
                        textURL.setText("")
                        haveURL = false
                    } catch (e: Exception) {
                        Log.e("Youtube API", "???????????? ???????????? ???????????? ??????????????? ??????????????????.")
                    }
                } else {
                    Toast.makeText(this, "????????? URL ??? ??????????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
                }

            }
        }

        /* ????????? ?????? */
        val imgConstruction1 = findViewById<ImageView>(R.id.imageView_home_construction1)
        val imgConstruction2 = findViewById<ImageView>(R.id.imageView_home_construction2)
        val imgConstruction3 = findViewById<ImageView>(R.id.imageView_home_construction3)
        val recentConstruction = findViewById<ImageView>(R.id.iamgeView_home_recent)
        imgConstruction1.setOnClickListener {
            Toast.makeText(this, "?????? ????????????.", Toast.LENGTH_SHORT).show()
        }
        imgConstruction2.setOnClickListener {
            Toast.makeText(this, "?????? ????????????.", Toast.LENGTH_SHORT).show()
        }
        imgConstruction3.setOnClickListener {
            Toast.makeText(this, "?????? ????????????.", Toast.LENGTH_SHORT).show()
        }
        recentConstruction.setOnClickListener {
            Toast.makeText(this, "?????? ????????????.", Toast.LENGTH_SHORT).show()
        }






        /* ?????????????????? ???????????? */
        val database = Firebase.database.reference.child("??????")
        database.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val title = snapshot.child("??????").value as String
                val channelTitle = snapshot.child("????????????").value as String
                val thumbnailURL = snapshot.child("????????? URL").value as String
                val url = snapshot.child("URL").value as String

                videos.add(Video(title, channelTitle, thumbnailURL, url))
                adapter.submitList(videos)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    /* ?????? */

    /* ??????????????? ????????????????????? ?????? */
    private fun saveVideoInfo(
        videoId: String,
        channelTitle: String,
        title: String,
        thumbNailURL: String,
        url: String
        ) {
        val user = auth.currentUser
        user?.let {
            val database = Firebase.database.reference.child("??????").child(videoId)
            val videoMap = mutableMapOf<String, Any>()
            videoMap["??????"] = title
            videoMap["????????????"] = channelTitle
            videoMap["????????? URL"] = thumbNailURL
            videoMap["URL"] = url
            database.updateChildren(videoMap)
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        val photoUrl = auth.currentUser?.photoUrl

        // Check if user is signed in (non-null) and update UI accordingly.
        Firebase.database.reference.child("?????????").child(auth.currentUser?.uid.toString()).child("?????????").get().addOnSuccessListener { email ->

            /* ????????????????????? ????????? ???????????? */
            if (email.value == null) {
                auth.signOut()
                googleSignInClient.signOut()
                updateUI(auth.currentUser)
            } else {
                Glide.with(this)
                    .load(photoUrl)
                    .into(imgProfile)
            }
        }
    }
    // [END on_start_check_user]

    /* ??????????????? ?????? */
    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }

}
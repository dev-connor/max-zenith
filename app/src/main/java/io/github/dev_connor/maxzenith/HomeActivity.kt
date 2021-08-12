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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        adapter = YoutubeAdapter()
        setContentView(binding.root)
        binding.recyclerViewHomeYoutube.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomeYoutube.adapter = adapter

        auth = Firebase.auth




        /* 자동로그인 해제 */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        /* findViewById */
        val editId = findViewById<EditText>(R.id.editText_home_id)
        imgProfile = findViewById<ImageView>(R.id.imageView_home_profile)
        val imgPost = findViewById<ImageView>(R.id.iamgeView_home_post)

        /* 버튼 */
        /* 텍스트지우기 버튼 */
        val imgDelete = findViewById<ImageView>(R.id.imageView_home_delete)
        imgDelete.setOnClickListener {
            editId.setText("")
        }

        /* 사용자정보 탭 버튼 */
        imgProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        /* 클립보드 붙여넣기 버튼 */
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        imgPost.setOnClickListener {
            if (!clipboard.hasPrimaryClip()) {
                Toast.makeText(this, "복사한 URL 이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            val pasteData = clipboard.primaryClip?.getItemAt(0)?.text as String
            if (pasteData.contains("list=")) {
                Toast.makeText(this, "재생목록 입니다.", Toast.LENGTH_SHORT).show()
            } else if (pasteData.contains("v=")) {
                Toast.makeText(this, "단일영상 입니다.", Toast.LENGTH_SHORT).show()
            } else if (pasteData.contains("youtu.be/")) {
                Toast.makeText(this, "단일영상 입니다.", Toast.LENGTH_SHORT).show()
            } else {}
            editId.setText(pasteData)
        }

        /* 공사중 버튼 */
        val imgConstruction1 = findViewById<ImageView>(R.id.imageView_home_construction1)
        val imgConstruction2 = findViewById<ImageView>(R.id.imageView_home_construction2)
        val imgConstruction3 = findViewById<ImageView>(R.id.imageView_home_construction3)
        val recentConstruction = findViewById<ImageView>(R.id.iamgeView_home_recent)
        imgConstruction1.setOnClickListener {
            Toast.makeText(this, "준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
        imgConstruction2.setOnClickListener {
            Toast.makeText(this, "준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
        imgConstruction3.setOnClickListener {
            Toast.makeText(this, "준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
        recentConstruction.setOnClickListener {
            Toast.makeText(this, "준비 중입니다.", Toast.LENGTH_SHORT).show()
        }


        /* 레트로핏: API 라이브러리 */
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val youtubeService = retrofit.create(YoutubeService::class.java)

        /* 데이터베이스저장 버튼 */
        val btnAddList = findViewById<Button>(R.id.button_home_addList)
        btnAddList.setOnClickListener {
            val youtubeURL = editId.text.toString()
            var videoId = ""

            /* URL 이 재생목록일 경우 */
            if (youtubeURL.contains("list=")) {
                try {
                    videoId = youtubeURL.substring(youtubeURL.indexOf("list=") + 5)
                } catch (e: Exception) {
                    Toast.makeText(this, "URL 이 너무 짧습니다.", Toast.LENGTH_SHORT).show()
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
                                    } catch (e: Exception) {
                                        Log.e("Youtube API", "리스트가 아니거나 데이터를 가져오는데 실패했습니다.")
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<Youtube>, t: Throwable) {
                            Log.e("Youtube API", "유튜브 API 를 가져오는데 실패했습니다.")
                        }
                    })

                /* URL 이 재생목록이 아닐 경우 */
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
                    saveVideoInfo(videoId, "채널명",
                        "제목",
                        imageLink, test)
                } catch (e: Exception) {
                    Log.e("Youtube API", "동영상이 아니거나 데이터를 가져오는데 실패했습니다.")
                }
            } else if (youtubeURL.contains("youtu.be/")){
                val test = youtubeURL
                val startIndex = test.indexOf("youtu.be/")
                videoId = test.substring(startIndex + 9)
                val imageLink = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg"
                try {
                    saveVideoInfo(videoId, "채널명",
                        "제목",
                        imageLink, test)
                } catch (e: Exception) {
                    Log.e("Youtube API", "동영상이 아니거나 데이터를 가져오는데 실패했습니다.")
                }
            } else {}
            editId.setText("")
        }

        /* 데이터베이스 불러오기 */
        val database = Firebase.database.reference.child("영상")
        database.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val title = snapshot.child("제목").value as String
                val channelTitle = snapshot.child("채널이름").value as String
                val thumbnailURL = snapshot.child("썸네일 URL").value as String
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

    /* 함수 */

    /* 비디오정보 데이터베이스에 저장 */
    private fun saveVideoInfo(
        videoId: String,
        channelTitle: String,
        title: String,
        thumbNailURL: String,
        url: String
        ) {
        val user = auth.currentUser
        user?.let {
            val database = Firebase.database.reference.child("영상").child(videoId)
            val videoMap = mutableMapOf<String, Any>()
            videoMap["제목"] = title
            videoMap["채널이름"] = channelTitle
            videoMap["썸네일 URL"] = thumbNailURL
            videoMap["URL"] = url
            database.updateChildren(videoMap)
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        val photoUrl = auth.currentUser?.photoUrl

        // Check if user is signed in (non-null) and update UI accordingly.
        Firebase.database.reference.child("사용자").child(auth.currentUser?.uid.toString()).child("이메일").get().addOnSuccessListener { email ->

            /* 데이터베이스가 없으면 로그아웃 */
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

    /* 로그인화면 이동 */
    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }

}
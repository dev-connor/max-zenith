package io.github.dev_connor.maxzenith

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var textEmail: TextView
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
        textEmail = findViewById(R.id.textView_home_email)
        val editId = findViewById<EditText>(R.id.editText_home_id)
        val textComment = findViewById<TextView>(R.id.textview_youtube_comment)

        /* 버튼 */

        /* 로그아웃 버튼 */
        val btnLogOut = findViewById<Button>(R.id.button_home_logOut)
        btnLogOut.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            updateUI(auth.currentUser)
        }

        /* 텍스트지우기 버튼 */
        val imgDelete = findViewById<ImageView>(R.id.imageView_home_delete)
        imgDelete.setOnClickListener {
            editId.setText("")
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
            val textId = editId.text.toString()
            var channelId = ""
            try {
                channelId = textId.substring(textId.indexOf("list=") + 5)
            } catch (e: Exception) {
                Toast.makeText(this, "올바른 URL 이 아닙니다.", Toast.LENGTH_SHORT).show()
            }
            youtubeService.getPlaylists(channelId)
                .enqueue(object : Callback<Youtube> {
                    override fun onResponse(
                        call: Call<Youtube>,
                        response: Response<Youtube>
                    ) {
                        // todo 성공처리
                        if (response.isSuccessful.not()) {
                            Log.e("TAG", "Not!! Success")
                            return
                        }
                        response.body()?.let {
                            it.items.forEach {
                                val video = it.snippet
                                saveVideoInfo(video.channelTitle,
                                    video.title,
                                    video.thumbnails.maxres.url)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Youtube>, t: Throwable) {
                        // TODO("Not yet implemented")
                    }
                })
            editId.setText("")
        }

        /* 데이터베이스 불러오기 */
        val database = Firebase.database.reference.child("영상")
        database.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val title = snapshot.child("제목").value.toString()
                val channelTitle = snapshot.child("채널이름").value.toString()
                val url = snapshot.child("썸네일 URL").value.toString()

                videos.add(Video(title, channelTitle, url))
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
        channelTitle: String,
        title: String,
        url: String
    ) {
        val user = auth.currentUser
        user?.let {
            val database = Firebase.database.reference.child("영상").child(title)
            val videoMap = mutableMapOf<String, Any>()
            videoMap["제목"] = title
            videoMap["채널이름"] = channelTitle
            videoMap["썸네일 URL"] = url
            database.updateChildren(videoMap)
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        Firebase.database.reference.child("사용자").child(auth.currentUser?.uid.toString()).child("이메일").get().addOnSuccessListener { email ->

            /* 데이터베이스가 없으면 로그아웃 */
            if (email.value == null) {
                auth.signOut()
                googleSignInClient.signOut()
                updateUI(auth.currentUser)
            } else {
                textEmail.setText("email: " + auth.currentUser?.email)
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
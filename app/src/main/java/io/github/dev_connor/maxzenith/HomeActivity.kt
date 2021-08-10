package io.github.dev_connor.maxzenith

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.dev_connor.maxzenith.data.Youtube
import io.github.dev_connor.maxzenith.data.YoutubeService
import io.github.dev_connor.maxzenith.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var textEmail: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: YoutubeAdapter
    private lateinit var binding: ActivityHomeBinding

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
        val imgVideo = findViewById<ImageView>(R.id.imageview_youtube_video)

        /* 로그아웃 버튼 */
        val btnLogOut = findViewById<Button>(R.id.button_home_logOut)
        btnLogOut.setOnClickListener{
            auth.signOut()
            googleSignInClient.signOut()
            updateUI(auth.currentUser)
        }

        /* 텍스트지우기 버튼 */
        val imgDelete = findViewById<ImageView>(R.id.imageView_home_delete)
        imgDelete.setOnClickListener{
            editId.setText("")
        }

        /* 리사이클러뷰 추가 버튼 */
        val btnAddList = findViewById<Button>(R.id.button_home_addList)
        btnAddList.setOnClickListener{

            /* 레트로핏: API 라이브러리 */
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val youtubeService = retrofit.create(YoutubeService::class.java)
            youtubeService.getPlaylists(editId.text.toString())
                .enqueue(object: Callback<Youtube> {
                    override fun onResponse(
                        call: Call<Youtube>,
                        response: Response<Youtube>
                    ) {
                        // todo 성공처리
                        if (response.isSuccessful.not()) {
                            Log.e("TAG", "Not!! Success")
                            return
                        }
                        response.body()?.let { it ->
                            Log.d("TAG", it.toString())
                            it.items.forEach {
                                Log.d("TAG", it.toString())
                            }
                            Log.d("TAG", it.items[0].toString())
                            adapter.submitList(it.items)
                        }
                    }

                    override fun onFailure(call: Call<Youtube>, t: Throwable) {
                        // TODO("Not yet implemented")
                    }
                })
            editId.setText("")

a
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(auth.currentUser)
        textEmail.setText("email: " + auth.currentUser?.email)
    }
    // [END on_start_check_user]

    /* 로그인화면 이동 */
    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }
}
package io.github.dev_connor.maxzenith

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.dev_connor.maxzenith.data.Youtube
import io.github.dev_connor.maxzenith.data.YoutubeService
import io.github.dev_connor.maxzenith.data.YoutubeTest
import io.github.dev_connor.maxzenith.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var textView_home_email: TextView
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

        var list = mutableListOf(
            YoutubeTest("사용자"),
        )
        adapter.submitList(list)

        /* 리스트 추가 */
        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            list.add(YoutubeTest(editText.text.toString()))
            adapter.notifyDataSetChanged()
        }

        /* 로그아웃 */
        val button_home_logOut = findViewById<Button>(R.id.button_home_logOut)
        button_home_logOut.setOnClickListener{
            auth.signOut()
            googleSignInClient.signOut()
            updateUI(auth.currentUser)
        }

        textView_home_email = findViewById(R.id.textView_home_email)

        /* 레트로핏: API 라이브러리 */
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val youtubeService = retrofit.create(YoutubeService::class.java)
        youtubeService.getPlaylists("AIzaSyBpiYuuOnCz7aKgBZEaldurIH8wfix7i88")
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
                    response.body()?.let {
                        Log.d("TAG", it.toString())
                        it.items.forEach {
                            Log.d("TAG", it.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<Youtube>, t: Throwable) {
                    // TODO("Not yet implemented")
                }
            })
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(auth.currentUser)
        textView_home_email.setText("email: " + auth.currentUser?.email)
    }
    // [END on_start_check_user]

    /* 로그인화면 이동 */
    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }


}
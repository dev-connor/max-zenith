package io.github.dev_connor.maxzenith

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private lateinit var textEmail: TextView
    private lateinit var textName: TextView
    private lateinit var imgProfileTab: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = Firebase.auth

        /* findViewById */
        imgProfileTab = findViewById<ImageView>(R.id.imageView_profile_profileTab)
        imgProfile = findViewById<ImageView>(R.id.imageView_profile_profile)
        val imgHome = findViewById<ImageView>(R.id.imageView_profile_home)
        textName = findViewById<TextView>(R.id.textView_profile_name)
        textEmail = findViewById<TextView>(R.id.textView_profile_email)

        /* 버튼 */
        /* 홈 탭 버튼 */
        imgHome.setOnClickListener {
            finish()
        }

        /* 로그아웃 버튼 */
        val btnLogout = findViewById<Button>(R.id.button_profile_logout)
        btnLogout.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            updateUI(auth.currentUser)
            finish()
        }

        /* 자동로그인 해제 */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

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
                textName.setText(auth.currentUser?.displayName)
                textEmail.setText(auth.currentUser?.email)
                Glide.with(this)
                    .load(photoUrl)
                    .into(imgProfileTab)
                Glide.with(this)
                    .load(photoUrl)
                    .into(imgProfile)
            }
        }
    }

    /* 로그인화면 이동 */
    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }
}
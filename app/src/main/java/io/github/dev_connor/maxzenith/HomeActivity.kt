package io.github.dev_connor.maxzenith

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.dev_connor.maxzenith.data.Youtube
import io.github.dev_connor.maxzenith.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
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

        var list = mutableListOf(
            Youtube("id1"),
            Youtube("id2"),
            Youtube("id3")
        )
        adapter.submitList(list)

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            list.add(Youtube(editText.text.toString()))
            adapter.notifyDataSetChanged()
        }

        val button_home_logOut = findViewById<Button>(R.id.button_home_logOut)
        button_home_logOut.setOnClickListener{
            auth.signOut()
            updateUI(auth.currentUser)
        }

        textView_home_email = findViewById<TextView>(R.id.textView_home_email)
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
        textView_home_email.setText("email: " + auth.currentUser?.email)
    }
    // [END on_start_check_user]

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }
}
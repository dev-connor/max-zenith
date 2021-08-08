package io.github.dev_connor.maxzenith

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.dev_connor.maxzenith.data.Youtube
import io.github.dev_connor.maxzenith.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var adapter: YoutubeAdapter
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        adapter = YoutubeAdapter()
        setContentView(binding.root)
        binding.recyclerViewHomeYoutube.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomeYoutube.adapter = adapter

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
    }
}
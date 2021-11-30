package com.example.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

const val MODE_MESSAGE = "com.example.note.MODE"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onAddNote(view: View) {
        val intent = Intent(this, CreateEditNote::class.java)
        intent.putExtra(MODE_MESSAGE, false)
        startActivity(intent)
    }
}
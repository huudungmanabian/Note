package com.example.note

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CreateEditNote : AppCompatActivity() {

    var isEdit: Boolean = false
    lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_edit_note)
        isEdit = intent.getBooleanExtra(MODE_MESSAGE, false)
        saveButton = findViewById(R.id.saveButton)

        saveButton.text = getString(if (isEdit) R.string.save_note else R.string.create_note)

    }
}
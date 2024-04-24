package com.example.edit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_PICK = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLoadImage: Button = findViewById(R.id.btnLoadImage)
        btnLoadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val intent = Intent(this, EditImageActivity::class.java)
            intent.putExtra("imageUri", selectedImage.toString())
            startActivity(intent)
        }
    }
}

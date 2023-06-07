package com.anu.firebasevideocallingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.anu.firebasevideocallingapp.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        var imageBack = findViewById<ImageView>(R.id.imageBack)
        var textSignIn = findViewById<TextView>(R.id.textSignIn)

        imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        textSignIn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}
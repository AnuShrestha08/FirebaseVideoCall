package com.anu.firebasevideocallingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
//import android.widget.Toast
import com.anu.firebasevideocallingapp.R
//import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var textSignUp = findViewById<TextView>(R.id.textSignUp)
        textSignUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

// code written for testing purpose only so that it can be seen in firebase
//            val database: FirebaseFirestore = FirebaseFirestore.getInstance()
//            val user: HashMap<String, Any> = HashMap()
//            user["first_name"] = "Anu"
//            user["last_name"] = "Shrestha"
//            user["email"] = "anu.shrestha@gmail.com"
//            database.collection("users")
//                .add(user)
//                .addOnSuccessListener { documentReference ->
//                    Toast.makeText(this@SignInActivity, "User Inserted", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this@SignInActivity, "User Failed to Insert", Toast.LENGTH_SHORT).show()
//                }
        }

    }
}
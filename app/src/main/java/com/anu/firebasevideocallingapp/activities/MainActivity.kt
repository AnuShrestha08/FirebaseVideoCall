package com.anu.firebasevideocallingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.adapters.UsersAdapters
import com.anu.firebasevideocallingapp.models.User
import com.anu.firebasevideocallingapp.utilities.Constants
import com.anu.firebasevideocallingapp.utilities.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var users: List<User>
    private lateinit var usersAdapters: UsersAdapters
    private lateinit var textErrorMessage: TextView
    private lateinit var usersProgressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(applicationContext)
        var textTitle = findViewById<TextView>(R.id.textTitle)
        textTitle.text = String.format(
            "%s %s",
            preferenceManager.getString(Constants.KEY_FIRST_NAME),
            preferenceManager.getString(Constants.KEY_LAST_NAME)
        )

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (token != null) {
                        sendFCMTokenToDatabase(token)
                    }
                }
            }

        var textSignOut = findViewById<TextView>(R.id.textSignOut)
        textSignOut.setOnClickListener {
            signOut()
        }

        var usersRecyclerView:RecyclerView=findViewById(R.id.usersRecyclerview)
        textErrorMessage = findViewById(R.id.textErrorMessage)
        usersProgressBar = findViewById(R.id.usersProgressBar)
        users = ArrayList()
        usersAdapters = UsersAdapters(users)
        usersRecyclerView.adapter=usersAdapters

    }



    private fun sendFCMTokenToDatabase(token: String) {
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USERS_ID)?: "")

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Token updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@MainActivity, "Unable to send token.${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signOut(){
        Toast.makeText(this@MainActivity, "Signing Out...", Toast.LENGTH_LONG).show()
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USERS_ID)?: "")
            val updates =hashMapOf<String, Any>(Constants.KEY_FCM_TOKEN to FieldValue.delete())
        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clearPreferences()
                startActivity(Intent(applicationContext,SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                Toast.makeText(this@MainActivity, "Unable to sign out",Toast.LENGTH_SHORT).show()
            }



    }
}
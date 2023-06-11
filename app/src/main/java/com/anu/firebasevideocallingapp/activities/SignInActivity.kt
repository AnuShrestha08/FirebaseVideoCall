package com.anu.firebasevideocallingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.utilities.Constants
import com.anu.firebasevideocallingapp.utilities.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var buttonSignIn: MaterialButton

    private lateinit var signInProgressBar: ProgressBar
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var textSignUp = findViewById<TextView>(R.id.textSignUp)
        textSignUp.setOnClickListener {
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

        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        signInProgressBar = findViewById(R.id.signInProgressBar)

        preferenceManager = PreferenceManager(applicationContext)
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonSignIn.setOnClickListener { v ->
            if (inputEmail.text.toString().trim().isEmpty()) {
                Toast.makeText(this@SignInActivity, "Enter email", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString()).matches()) {
                Toast.makeText(this@SignInActivity, "Enter valid email", Toast.LENGTH_SHORT).show()
            } else if (inputPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this@SignInActivity, "Enter password", Toast.LENGTH_SHORT).show()
            } else {
                signIn()
            }
        }

    }

    private fun signIn() {
        buttonSignIn.visibility = View.INVISIBLE
        signInProgressBar.visibility = View.VISIBLE
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                    val documentSnapshot = task.result!!.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(
                        Constants.KEY_FIRST_NAME,
                        documentSnapshot.getString(Constants.KEY_FIRST_NAME)?: ""
                    )
                    preferenceManager.putString(
                        Constants.KEY_LAST_NAME,
                        documentSnapshot.getString(Constants.KEY_LAST_NAME)?: ""
                    )
                    preferenceManager.putString(
                        Constants.KEY_EMAIL,
                        documentSnapshot.getString(Constants.KEY_EMAIL)?: ""
                    )
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    signInProgressBar.visibility = View.INVISIBLE
                    buttonSignIn.visibility = View.VISIBLE
                    Toast.makeText(this@SignInActivity, "Unable to sign in", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}


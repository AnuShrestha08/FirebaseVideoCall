package com.anu.firebasevideocallingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.utilities.Constants
import com.anu.firebasevideocallingapp.utilities.PreferenceManager
import android.content.SharedPreferences
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var inputFirstName : EditText
    private lateinit var inputLastName : EditText
    private lateinit var inputEmail : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputConfirmPassword : EditText
    private lateinit var buttonSignUp : MaterialButton
    private lateinit var signUpProgressBar: ProgressBar
    private lateinit var preferenceManager: PreferenceManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        preferenceManager = PreferenceManager(applicationContext)

        var imageBack = findViewById<ImageView>(R.id.imageBack)
        var textSignIn = findViewById<TextView>(R.id.textSignIn)

        imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        textSignIn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        inputFirstName = findViewById(R.id.inputFirstName)
        inputLastName = findViewById(R.id.inputLastName)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword= findViewById(R.id.inputConfirmPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        signUpProgressBar = findViewById(R.id.signUpProgressBar)

        buttonSignUp.setOnClickListener { v ->
                if (inputFirstName.text.toString().trim().isEmpty()) {
                    Toast.makeText(this@SignUpActivity, "Enter first name", Toast.LENGTH_SHORT).show()
                } else if (inputLastName.text.toString().trim().isEmpty()) {
                    Toast.makeText(this@SignUpActivity, "Enter last name", Toast.LENGTH_SHORT).show()
                }else if (inputEmail.text.toString().trim().isEmpty()) {
                    Toast.makeText(this@SignUpActivity, "Enter last name", Toast.LENGTH_SHORT).show()
                }else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString()).matches()) {
                    Toast.makeText(this@SignUpActivity, "Enter valid email", Toast.LENGTH_SHORT).show()
                } else if (inputPassword.text.toString().trim().isEmpty()) {
                    Toast.makeText(this@SignUpActivity, "Enter password", Toast.LENGTH_SHORT).show()
                } else if (inputConfirmPassword.text.toString().trim().isEmpty()) {
                    Toast.makeText(this@SignUpActivity, "Confirm your password", Toast.LENGTH_SHORT).show()
                } else if (inputPassword.text.toString() != inputConfirmPassword.text.toString()) {
                    Toast.makeText(this@SignUpActivity, "Password and confirm password must be the same", Toast.LENGTH_SHORT).show()
                }else{
                    signUp()
                }
            }

        }

    private fun signUp(){
        buttonSignUp.visibility = View.INVISIBLE
        signUpProgressBar.visibility = View.VISIBLE

        val database = FirebaseFirestore.getInstance()
        val user = HashMap<String, Any>()
        user[Constants.KEY_FIRST_NAME] = inputFirstName.text.toString()
        user[Constants.KEY_LAST_NAME] = inputLastName.text.toString()
        user[Constants.KEY_EMAIL] = inputEmail.text.toString()
        user[Constants.KEY_PASSWORD] = inputPassword.text.toString()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener { documentReference ->
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                preferenceManager.putString(Constants.KEY_USERS_ID,
                    documentReference.id)
                preferenceManager.putString(Constants.KEY_FIRST_NAME,inputFirstName.text.toString())
                preferenceManager.putString(Constants.KEY_LAST_NAME,inputLastName.text.toString())
                preferenceManager.putString(Constants.KEY_EMAIL,inputEmail.text.toString())
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                signUpProgressBar.visibility = View.INVISIBLE
                buttonSignUp.visibility = View.VISIBLE
                Toast.makeText(this, "Error: " + exception.message, Toast.LENGTH_SHORT).show()


            }

    }



}




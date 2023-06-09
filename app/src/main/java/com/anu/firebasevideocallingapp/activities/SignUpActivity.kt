package com.anu.firebasevideocallingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import com.anu.firebasevideocallingapp.R
import com.google.android.material.button.MaterialButton

class SignUpActivity : AppCompatActivity() {

    private lateinit var inputFirstName : EditText
    private lateinit var inputLastName : EditText
    private lateinit var inputEmail : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputConfirmPassword : EditText
    private lateinit var buttonSignUp : MaterialButton
    private lateinit var signUpProgressBar: ProgressBar

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


        inputFirstName = findViewById(R.id.inputFirstName)
        inputLastName = findViewById(R.id.inputLastName)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword= findViewById(R.id.inputConfirmPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)

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

    }



}
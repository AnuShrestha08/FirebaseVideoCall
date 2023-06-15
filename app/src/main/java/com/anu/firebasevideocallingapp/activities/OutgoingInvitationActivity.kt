package com.anu.firebasevideocallingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.models.User

class OutgoingInvitationActivity : AppCompatActivity() {

    private lateinit var imageMeetingType:ImageView
    private lateinit var imageStopInvitation:ImageView
    private lateinit var textFirstChar:TextView
    private lateinit var textUsername:TextView
    private lateinit var textEmail:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_invitation)

        imageMeetingType = findViewById(R.id.imageMeetingType)
        val meetingType = intent.getStringExtra("type")

        if(meetingType!=null){
            if(meetingType=="video"){
                imageMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        textFirstChar = findViewById(R.id.textFirstChar)
        textUsername = findViewById(R.id.textUsername)
        textEmail = findViewById(R.id.textEmail)

        val user = intent.getSerializableExtra("user") as User?
        if(user != null){
            textFirstChar.text = user.firstName.substring(0,1)
            textUsername.text = String.format("%s%s",user.firstName,user.LastName)
            textEmail.text = user.email
        }

        imageStopInvitation = findViewById(R.id.imageStopInvitation)
        imageStopInvitation.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
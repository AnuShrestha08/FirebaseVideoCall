package com.anu.firebasevideocallingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.utilities.Constants

class IncomingInvitationActivity : AppCompatActivity() {

    private lateinit var imageMeetingType : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)

        imageMeetingType = findViewById(R.id.imageMeetingType )
        val meetingType: String? = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE)
        if (meetingType != null) {
            if (meetingType == "video") {
                imageMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        val textFirstChar: TextView = findViewById(R.id.textFirstChar)
        val textUsername: TextView = findViewById(R.id.textUsername)
        val textEmail: TextView = findViewById(R.id.textEmail)
        val firstName: String? = intent.getStringExtra(Constants.KEY_FIRST_NAME)
        if (firstName != null) {
            textFirstChar.text = firstName.substring(0, 1)
            textUsername.text = String.format("%s %s", firstName, intent.getStringExtra(Constants.KEY_LAST_NAME))
            textEmail.text = intent.getStringExtra(Constants.KEY_EMAIL)
        }
    }
}
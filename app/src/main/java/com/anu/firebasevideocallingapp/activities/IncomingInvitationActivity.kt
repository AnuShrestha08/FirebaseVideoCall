package com.anu.firebasevideocallingapp.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.network.ApiClient
import com.anu.firebasevideocallingapp.network.ApiService
import com.anu.firebasevideocallingapp.utilities.Constants
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        val imageAcceptInvitation: ImageView = findViewById(R.id.imageAcceptInvitation)
        imageAcceptInvitation.setOnClickListener {
            sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)?: ""
            )
        }

        val imageRejectInvitation: ImageView = findViewById(R.id.imageRejectInvitation)
        imageRejectInvitation.setOnClickListener {
            sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN) ?: ""
            )
        }


        }

    private fun sendInvitationResponse(type: String, receiverToken: String) {
        try {
            val tokens = JSONArray().apply { put(receiverToken) }


            val body = JSONObject().apply {
                val data = JSONObject().apply {
                    put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
                    put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

                }
                put(Constants.REMOTE_MSG_DATA, data)
                put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
            }

            sendRemoteMessage(body.toString(), type)

        } catch (exception: Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {
        ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
            Constants.getRemoteMessageHeaders(),
            remoteMessageBody
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // Handle the successful response here
                } else {
                    Toast.makeText(
                        this@IncomingInvitationActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    this@IncomingInvitationActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }
}
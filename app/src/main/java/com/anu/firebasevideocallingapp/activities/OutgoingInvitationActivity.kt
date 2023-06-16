package com.anu.firebasevideocallingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.models.User
import com.anu.firebasevideocallingapp.network.ApiClient
import com.anu.firebasevideocallingapp.network.ApiService
import com.anu.firebasevideocallingapp.utilities.Constants
import com.anu.firebasevideocallingapp.utilities.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OutgoingInvitationActivity : AppCompatActivity() {

    private lateinit var imageMeetingType:ImageView
    private lateinit var imageStopInvitation:ImageView
    private lateinit var textFirstChar:TextView
    private lateinit var textUsername:TextView
    private lateinit var textEmail:TextView

    private lateinit var preferenceManager : PreferenceManager
    private var inviterToken:String? = null

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

        preferenceManager = PreferenceManager(applicationContext)
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task->
            if(task.isSuccessful && task.result != null){
                inviterToken = task.result

            }

        }

        if (meetingType != null && user != null) {
            initiateMeeting(meetingType, user.token)
        }

    }

    private fun initiateMeeting(meetingType: String, receiverToken: String) {
        try {
            val tokens = JSONArray().apply { put(receiverToken) }

            val body = JSONObject().apply {
                val data = JSONObject().apply {
                    put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION)
                    put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType)
                    put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME))
                    put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME))
                    put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL))
                    put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken)
                }
                put(Constants.REMOTE_MSG_DATA, data)
                put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
            }

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION)


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
                    if (type == Constants.REMOTE_MSG_INVITATION) {
                        Toast.makeText(
                            this@OutgoingInvitationActivity,
                            "Invitation sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@OutgoingInvitationActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    this@OutgoingInvitationActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }


}
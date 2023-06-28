package com.anu.firebasevideocallingapp.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.models.User
import com.anu.firebasevideocallingapp.network.ApiClient
import com.anu.firebasevideocallingapp.network.ApiService
import com.anu.firebasevideocallingapp.utilities.Constants
import com.anu.firebasevideocallingapp.utilities.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*

class OutgoingInvitationActivity : AppCompatActivity() {

    private lateinit var imageMeetingType:ImageView
    private lateinit var imageStopInvitation:ImageView
    private lateinit var textFirstChar:TextView
    private lateinit var textUsername:TextView
    private lateinit var textEmail:TextView

    private lateinit var preferenceManager : PreferenceManager
    private var inviterToken:String? = null
    private var meetingRoom:String? = null

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
            if(user!=null){
                cancelInvitation(user.token)
            }
            finish()
        }

        preferenceManager = PreferenceManager(applicationContext)

        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task->
            if(task.isSuccessful && task.result != null){
                inviterToken = task.result

                if (meetingType != null && user != null) {
                    initiateMeeting(meetingType, user.token)
                }

            }
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

                    meetingRoom = preferenceManager.getString(Constants.KEY_USERS_ID)+"_"+
                            UUID.randomUUID().toString().substring(0,5)

                    put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom)

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
                        Toast.makeText(this@OutgoingInvitationActivity, "Invitation sent successfully", Toast.LENGTH_LONG).show()
                    } else if(type == Constants.REMOTE_MSG_INVITATION_RESPONSE){
                        Toast.makeText(this@OutgoingInvitationActivity, "Invitation Cancelled", Toast.LENGTH_LONG).show()
                        //finish() //app nai close hunxa
                    }

                }else{
                        Toast.makeText(this@OutgoingInvitationActivity, response.message(), Toast.LENGTH_SHORT).show()
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

    private fun cancelInvitation(receiverToken: String) {
        try {
            val tokens = JSONArray().apply { put(receiverToken) }

            val body = JSONObject().apply {
                val data = JSONObject().apply {
                    put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
                    put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED)
                }

                put(Constants.REMOTE_MSG_DATA, data)
                put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
            }

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        } catch (exception: Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun invitationResponseReceiver() = object :BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if(type != null){
                if(type == Constants.REMOTE_MSG_INVITATION_ACCEPTED){
                    //Toast.makeText(context, "Invitation Accepted", Toast.LENGTH_SHORT).show()
                    try{
                        val serverURL = URL("https://meet.jit.si")
                        val conferenceOptions = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setFeatureFlag("welcomepage.enabled", false)
                            .setRoom(meetingRoom)
                            .build()
                        JitsiMeetActivity.launch(this@OutgoingInvitationActivity,conferenceOptions)
                        finish()
                    }catch(exception:Exception ){
                        Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }else if(type == Constants.REMOTE_MSG_INVITATION_REJECTED){
                    Toast.makeText(context, "Invitation REJECTED", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onStart(){
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(invitationResponseReceiver(),
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(invitationResponseReceiver())
    }


}
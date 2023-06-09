package com.anu.firebasevideocallingapp.firebase

//import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService (){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
//        Log.d("FCM","Token : $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
//        message.notification?.let{ notification ->
//            Log.d("FCM", "Remote message received: ${notification.body}")
//        }
    }
}
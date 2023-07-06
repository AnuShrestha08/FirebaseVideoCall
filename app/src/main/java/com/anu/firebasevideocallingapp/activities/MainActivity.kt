package com.anu.firebasevideocallingapp.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.ImageView
//import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.adapters.UsersAdapters
import com.anu.firebasevideocallingapp.listeners.UsersListener
import com.anu.firebasevideocallingapp.models.User
import com.anu.firebasevideocallingapp.utilities.Constants
import com.anu.firebasevideocallingapp.utilities.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), UsersListener {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var users: MutableList<User>
    private lateinit var usersAdapters: UsersAdapters
    private lateinit var textErrorMessage: TextView
   // private lateinit var usersProgressBar: ProgressBar
   private lateinit var swipeRefreshLayout: SwipeRefreshLayout
   private lateinit var imageConference: ImageView

   private var REQUEST_CODE_BATTERY_OPTIMIZATIONS : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(applicationContext)

        imageConference = findViewById(R.id.imageConference)

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
        //usersProgressBar = findViewById(R.id.usersProgressBar)
        users = ArrayList()
        usersAdapters = UsersAdapters(users, this)
        usersRecyclerView.adapter=usersAdapters

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            getUsers()
        }

        getUsers()

        checkForBatteryOptimizations()

    }

    private fun getUsers() {
        swipeRefreshLayout.isRefreshing= true
        //usersProgressBar.visibility = View.VISIBLE
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                //usersProgressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                val myUserId = preferenceManager.getString(Constants.KEY_USERS_ID)
                if (task.isSuccessful && task.result != null) {
                    users.clear()
                    for (documentSnapshot in task.result!!) {
                        if (myUserId == documentSnapshot.id) {
                            continue
                        }
                        val user = User()
                        user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME) ?: ""
                        user.LastName = documentSnapshot.getString(Constants.KEY_LAST_NAME) ?: ""
                        user.email = documentSnapshot.getString(Constants.KEY_EMAIL) ?: ""
                        user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN) ?: ""
                        users.add(user)
                    }

                    if (users.size > 0) {
                        usersAdapters.notifyDataSetChanged()
                    } else {
                        textErrorMessage.text = String.format("%s", "No users available")
                        textErrorMessage.visibility = View.VISIBLE
                    }
                } else {
                    textErrorMessage.text = String.format("%s", "No users available")
                    textErrorMessage.visibility = View.VISIBLE
                }
            }
        }

    private fun sendFCMTokenToDatabase(token: String) {
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USERS_ID)?: "")

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Signing in...", Toast.LENGTH_SHORT).show()
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

    override fun initiateVideoMeeting(user: User) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                this,
                "${user.firstName} ${user.LastName}: not available for Video meeting",
                Toast.LENGTH_SHORT
            ).show()
        } else {
//            Toast.makeText(
//                this,
//                "Video meeting with ${user.firstName} ${user.LastName}",
//                Toast.LENGTH_SHORT
//            ).show()
            val intent = Intent(applicationContext, OutgoingInvitationActivity::class.java)
            intent.putExtra("user",user)
            intent.putExtra("type","video")
            startActivity(intent)
            finish()
        }
    }

    override fun initiateAudioMeeting(user: User) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                this,
                "${user.firstName} ${user.LastName}: not available for Audio meeting",
                Toast.LENGTH_SHORT
            ).show()
        } else {
//            Toast.makeText(this, "Audio meeting with ${user.firstName} ${user.LastName}", Toast.LENGTH_SHORT
//            ).show()
            val intent = Intent(applicationContext, OutgoingInvitationActivity::class.java)
            intent.putExtra("user",user)
            intent.putExtra("type","audio")
            startActivity(intent)
            finish()
        }
    }

    override fun onMultipleUserAction(isMultipleUsersSelected: Boolean) {
        if(isMultipleUsersSelected){
            imageConference.visibility = View.VISIBLE
            imageConference.setOnClickListener {
                val intent = Intent(applicationContext, OutgoingInvitationActivity::class.java)
                intent.putExtra("selectedUsers", Gson().toJson(usersAdapters.getSelectedUsers()))
                intent.putExtra("type", "video")
                intent.putExtra("isMultiple", true)
                startActivity(intent)
            }
        }else{
            imageConference.visibility = View.GONE
        }
    }

    private fun checkForBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Warning")
                builder.setMessage("Battery optimization is enabled. It can interrupt running background services.")
                builder.setPositiveButton("Disable") { dialog, _ ->
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATIONS)
                    dialog.dismiss()
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS){
            checkForBatteryOptimizations()
        }
    }

}
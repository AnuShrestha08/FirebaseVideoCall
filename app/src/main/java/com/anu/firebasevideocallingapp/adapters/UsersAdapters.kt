package com.anu.firebasevideocallingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.listeners.UsersListener
import com.anu.firebasevideocallingapp.models.User

class UsersAdapters(private val users:List<User>,
                    private val usersListener: UsersListener
                    ) : RecyclerView.Adapter<UsersAdapters.ViewHolder>(){

    private val selectedUsers:MutableList<User> = mutableListOf()
    fun getSelectedUsers(): List<User>{
        return selectedUsers
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textFirstChar : TextView = itemView.findViewById(R.id.textFirstChar)
        val textUsername : TextView = itemView.findViewById(R.id.textUsername)
        val textEmail : TextView = itemView.findViewById(R.id.textEmail)
        val imageAudioMeeting : ImageView = itemView.findViewById(R.id.imageAudioMeeting)
        val imageVideoMeeting : ImageView = itemView.findViewById(R.id.imageVideoMeeting)

        var userContainer :ConstraintLayout = itemView.findViewById(R.id.userContainer)
        val imageSelected : ImageView = itemView.findViewById(R.id.imageSelected)

        fun setUserData(user:User){
            textFirstChar.text=user.firstName.substring(0,1)
            textUsername.text="${user.firstName} ${user.LastName}"
            textEmail.text = user.email

            imageAudioMeeting.setOnClickListener {
                usersListener.initiateAudioMeeting(user)
            }

            imageVideoMeeting.setOnClickListener {
                usersListener.initiateVideoMeeting(user)
            }

            userContainer.setOnLongClickListener { v ->
                if(imageSelected.visibility != View.VISIBLE){
                    selectedUsers.add(user)
                    imageSelected.visibility = View.VISIBLE
                    imageVideoMeeting.visibility = View.GONE
                    imageAudioMeeting.visibility = View.GONE
                    usersListener.onMultipleUserAction(true)
                }
                true
            }
            userContainer.setOnClickListener {
                if(imageSelected.visibility==View.VISIBLE){
                    selectedUsers.remove(user)
                    imageSelected.visibility = View.GONE
                    imageVideoMeeting.visibility = View.VISIBLE
                    imageAudioMeeting.visibility = View.VISIBLE
                    if(selectedUsers.size == 0){
                        usersListener.onMultipleUserAction(false)
                    }
                }else{
                    if(selectedUsers.size > 0){
                        selectedUsers.add(user)
                        imageSelected.visibility = View.VISIBLE
                        imageVideoMeeting.visibility = View.GONE
                        imageAudioMeeting.visibility = View.GONE
                    }
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_container_user,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.setUserData(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }


}
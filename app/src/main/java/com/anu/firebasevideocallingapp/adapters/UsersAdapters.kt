package com.anu.firebasevideocallingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anu.firebasevideocallingapp.R
import com.anu.firebasevideocallingapp.models.User

class UsersAdapters(private val users:List<User>) : RecyclerView.Adapter<UsersAdapters.ViewHolder>(){

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textFirstChar : TextView = itemView.findViewById(R.id.textFirstChar)
        val textUsername : TextView = itemView.findViewById(R.id.textUsername)
        val textEmail : TextView = itemView.findViewById(R.id.textEmail)
        val imageAudioMeeting : ImageView = itemView.findViewById(R.id.imageAudioMeeting)
        val imageVideoMeeting : ImageView = itemView.findViewById(R.id.imageVideoMeeting)

        fun setUserData(user:User){
            textFirstChar.text=user.firstName.substring(0,1)
            textUsername.text="${user.firstName} ${user.LastName}"
            textEmail.text = user.email
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
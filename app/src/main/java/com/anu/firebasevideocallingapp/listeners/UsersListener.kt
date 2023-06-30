package com.anu.firebasevideocallingapp.listeners

import com.anu.firebasevideocallingapp.models.User

interface UsersListener {

    fun initiateVideoMeeting(user: User)
    fun initiateAudioMeeting(user: User)
    fun onMultipleUserAction(isMultipleUsersSelected: Boolean)

}
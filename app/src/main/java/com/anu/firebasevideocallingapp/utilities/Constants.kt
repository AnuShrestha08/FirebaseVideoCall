package com.anu.firebasevideocallingapp.utilities

class Constants {

    companion object {
        const val KEY_COLLECTION_USERS = "users"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        const val KEY_PREFERENCE_NAME = "videoMeetingPreference"
        const val KEY_IS_SIGNED_IN = "isSignedIn"
        const val KEY_USERS_ID = "users_id"
        const val KEY_FCM_TOKEN= "fcm_token"

        const val REMOTE_MSG_AUTHORIZATION = "Authorization"
        const val REMOTE_MSG_CONTENT_TYPE = "Content-type"


        const val REMOTE_MSG_TYPE = "type"
        const val REMOTE_MSG_INVITATION = "invitation"
        const val REMOTE_MSG_MEETING_TYPE = "meeting-type"
        const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
        const val REMOTE_MSG_DATA = "data"
        const val REMOTE_MSG_REGISTRATION_IDS= "registration_ids"

        fun getRemoteMessageHeaders(): HashMap<String, String> {
            val headers = HashMap<String, String>()
            headers[Constants.REMOTE_MSG_AUTHORIZATION] =
                "key=AAAAb373sbw:APA91bGrelzovy9J_UZhqNFgUzdZaRqNKRt6LIA2LcDLAoYcZ2Etxhrv41AXQqghyDLqtZeoPlJGqNWi_1HIkLdeH8Lw6yf6qaNhQ32JUpG0or2g34uuNKmpuyrqj-E4TqKl3_fdPY_j"
            headers[Constants.REMOTE_MSG_CONTENT_TYPE] = "application/json"
            return headers
        }

    }


}
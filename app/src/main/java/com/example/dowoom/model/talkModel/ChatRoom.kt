package com.example.dowoom.model.talkModel

import android.net.Uri
import com.example.dowoom.model.User
import com.google.firebase.database.Exclude

//talk fragment
data class ChatRoom(
                    var from:String? = null,
                    var to:String? = null,
                    var message:String? = null,
                    var date:Long? = null,
                    var read:Boolean = false,
                    var user: User? = null
                    ) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "from" to from,
            "to" to to,
            "message" to message,
            "date" to date,
            "read" to read,
            "user" to user,
        )
    }
}

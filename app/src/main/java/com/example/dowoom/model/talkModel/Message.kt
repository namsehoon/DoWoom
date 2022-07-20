package com.example.dowoom.model.talkModel

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

//채팅방 내
@IgnoreExtraProperties
data class Message(
                   var from:String? = null,
                   var to:String? = null,
                   var imageUrl:String? = null,
                   var message:String? = null,
                   var messageId:String? = null,
                   var date:Long? = null,
                   var read:Boolean = false) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "from" to from,
            "to" to to,
            "imageUrl" to imageUrl,
            "message" to message,
            "messageId" to messageId,
            "date" to date,
            "read" to read
        )
    }


}
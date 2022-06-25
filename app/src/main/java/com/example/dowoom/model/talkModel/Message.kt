package com.example.dowoom.model.talkModel

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

//채팅방 내
@IgnoreExtraProperties
data class Message(
                   var sender:String? = null,
                   var otherUid:String? = null,
                   var imageUrl:String? = null,
                   var message:String? = null,
                   var messageId:String? = null,
                   var timeStamp:Long? = null,
                   var readOrNot:Boolean = false) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "sender" to sender,
            "otherUid" to otherUid,
            "imageUrl" to imageUrl,
            "message" to message,
            "messageId" to messageId,
            "timeStamp" to timeStamp,
            "readOrNot" to readOrNot
        )
    }
}
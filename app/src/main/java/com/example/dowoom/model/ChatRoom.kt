package com.example.dowoom.model

import com.google.firebase.database.Exclude

//talk fragment
data class ChatRoom(
                    var profileImg:String? = null,
                    var otherUid:String? = null,
                    var nickname:String? = null,
                    var lastMessage:String? = null,
                    var timeStamp:Long? = null,
                    var readOrNot:Boolean = false) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "profileImg" to profileImg,
            "otherUid" to otherUid,
            "nickname" to nickname,
            "lastMessage" to lastMessage,
            "timeStamp" to timeStamp,
            "readOrNot" to readOrNot
        )
    }
}

package com.example.dowoom.model

import android.R.attr
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import android.R.attr.author







//talk fragment
@IgnoreExtraProperties
class ChatRoom {
    var profileImg:String? = null
    var otherUid:String? = null
    var nickname:String? = null
    var lastMessage:String? = null
    var timeStamp:Long? = null
    var readOrNot:Boolean = false
    var chatId:String? = null
    var member: Member? = null


    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    constructor(profileImg: String?, otherUid: String?, nickname: String?, lastMessage: String?,timeStamp:Long?,readOrNot:Boolean,chatId:String?, member: Member? ) {
        this.profileImg = profileImg
        this.otherUid = otherUid
        this.nickname = nickname
        this.lastMessage = lastMessage
        this.timeStamp = timeStamp
        this.readOrNot = readOrNot
        this.chatId = chatId
        this.member = member
    }

    constructor(lastMessage: String?, timeStamp: Long?)  {
        this.lastMessage = lastMessage
        this.timeStamp = timeStamp
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result["profileImg"] = profileImg
        result["otherUid"] = otherUid
        result["nickname"] = nickname
        result["lastMessage"] = lastMessage
        result["timeStamp"] = timeStamp
        result["readOrNot"] = readOrNot
        result["chatId"] = chatId
        result["member"] = member

        return result
    }
}

@IgnoreExtraProperties
class Post {
    var uid: String? = null
    var author: String? = null
    var title: String? = null
    var body: String? = null
    var starCount = 0
    var stars: Map<String, Boolean> = HashMap()

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    constructor(uid: String?, author: String?, title: String?, body: String?) {
        this.uid = uid
        this.author = author
        this.title = title
        this.body = body
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result["uid"] = uid
        result["author"] = author
        result["title"] = title
        result["body"] = body
        result["starCount"] = starCount
        result["stars"] = stars
        return result
    }
}


package com.example.dowoom.model
//채팅방 내
data class Message(var chatUid:String? = null,
                   var sender:String? = null,
                   var imageUrl:String? = null,
                   var message:String? = null,
                   var timeStamp:Long? = null,
                   var readOrNot:Boolean = false)
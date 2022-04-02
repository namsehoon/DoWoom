package com.example.dowoom.model
//talk fragment
data class ChatRoom(var chatUid:String? = null,
                    var myUid:String? = null,
                    var otherUid:String? = null,
                    var nickname:String? = null,
                    var lastMessage:String? = null ,
                    var profileImg:String? = null,
                    var timeStamp:Long? = null,
                    var readOrNot:Boolean = false)

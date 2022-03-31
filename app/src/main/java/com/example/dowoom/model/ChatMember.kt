package com.example.dowoom.model
//채팅방 내
data class ChatMember(var chatUid:String? = null,
                      var toUid:String? = null,
                      var fromUid:String? = null,
                      var nickname:String?= null)
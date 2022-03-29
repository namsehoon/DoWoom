package com.example.dowoom.model

data class Message(var uid:String, var fromId:String? = null, var imageUrl:String? = null,  var message:String? = null, var timeStamp:Long? = null,var readOrNot:Boolean = false)
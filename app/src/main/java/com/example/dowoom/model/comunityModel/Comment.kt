package com.example.dowoom.model.comunityModel

data class Comment (
    var contentUid:String? = null,
    var commentUid:String? = null,
    var nickname:String? = null,
    var time:Long? = null,
    var comment:String? = null,
    //todo : 답글?
    )
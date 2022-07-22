package com.example.dowoom.model.comunityModel

data class Comment (
    var comuUid:String? = null,
    var commentUid:String? = null,
    var nickname:String? = null,
    var timeStamp:Long? = null,
    var comment:String? = null,
    //todo : 답글? (not now!)
    )
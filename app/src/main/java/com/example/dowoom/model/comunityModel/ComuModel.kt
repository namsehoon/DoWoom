package com.example.dowoom.model.comunityModel

import com.example.dowoom.model.talkModel.Member
import com.google.firebase.database.Exclude

//talk fragment
data class ComuModel(
    var contentUid:String? = null,
    var title:String? = null,
    var kindOf:Int?= null, // 베스트, 유머, 익명 중에
    var commentCount:Int? = null,
    var creator:String? = null,
    var timestamp:Long? = null,
    var alreadySee:Boolean? = false //만약 본거면 true 처리하면서 회색 ㄱ
)


data class Data(var content:ArrayList<ComuModel>)

data class Gezip(val data: Data)
package com.example.dowoom.model.comunityModel

import com.example.dowoom.model.talkModel.Member
import com.google.firebase.database.Exclude

//talk fragment
data class ComuModel(
    var uid:String? = null,
    var title:String? = null,
    var kindOf:Int?= null, // 베스트, 실시간게시판, 익명 중에
    var commentCount:Int? = null,
    var creator:String? = null,
    var alreadySee:Boolean? = false, //만약 본거면 true 처리하면서 회색 ㄱ
    var contentLocation:String? = null // 이미지 or gif 위치
)


data class Data(var content:ArrayList<ComuModel>)

data class Gezip(val data: Data)
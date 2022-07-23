package com.example.dowoom.model.comunityModel

//talk fragment
data class ComuModel(
    var uid:String? = null,
    var title:String? = null,
    var kindOf:Int?= null, // 베스트, 실시간게시판, 익명 중에
    var commentCount:Int? = null,
    var creator:String? = null,
    var alreadySee:Boolean? = false, //만약 본거면 true 처리하면서 회색 ㄱ
    var contentImg:ArrayList<String>? = null, // img 있으면,,
    var contentText:String? = null, // 익명 글
    var recommend:Int? = null,
    var searchCount:Int? = null,
    var page:String? = null,
//    var imageKind:String? =null //jpg? gif?
)


data class Data(var content:ArrayList<ComuModel>)

data class Gezip(val data: Data)
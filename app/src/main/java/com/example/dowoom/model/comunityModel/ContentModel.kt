package com.example.dowoom.model.comunityModel

data class ContentModel(
    var contentImg:ArrayList<String>? = null, // img 있으면,,
    var contentText:String? = null, // 익명 글
    var recommend:Int? = null,
    var searchCount:Int? = null,
    )
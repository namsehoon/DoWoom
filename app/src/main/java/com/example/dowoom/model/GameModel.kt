package com.example.dowoom.model

data class GameModel(val title:String, val nickname:String, val gameUid:String, val acceptable:Int, val leftCount:Int, val active:Boolean, val whatKindGame:Int, var gameResult:GameResultModel? = null)

data class PresentModel(var gameUid: String, val presentList:Present? = null)
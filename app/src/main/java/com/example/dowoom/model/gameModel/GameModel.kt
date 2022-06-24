package com.example.dowoom.model.gameModel

import com.google.firebase.database.Exclude

data class GameModel(var title:String? = null,
                     var nickname:String? = null,
                     var gameUid:String? = null,
                     var acceptable:Int? = 6,
                     var leftCount:Int? = 0,
                     var active:Boolean? = true,
                     var whatKindGame:Int? = 0,
                     var gameResult: GameResultModel? = null) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "nickname" to nickname,
            "gameUid" to gameUid,
            "acceptable" to acceptable,
            "leftCount" to leftCount,
            "active" to active,
            "whatKindGame" to whatKindGame,
            "gameResult" to gameResult

        )
    }
}

data class PresentModel(var gameUid: String, val presentList: Present? = null)
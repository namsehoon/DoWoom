package com.example.dowoom.model.gameModel

import com.example.dowoom.model.User

//todo 하루에 2번이상 참여시 없어짐
data class GameParticipants(var data:String, var count:Int = 0, var nickname: User)
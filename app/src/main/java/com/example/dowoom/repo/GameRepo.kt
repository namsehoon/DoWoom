package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.activity.game.CreateGameActivity
import com.example.dowoom.model.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameRepo : repo {

    val gameRef = rootRef.child("Game")

    //사다리게임
    val ladderRef = rootRef.child("Game/Ladder")

    //돌림판
    val circleRef = rootRef.child("Game/Circle")

    //선착순
    val fasterRef = rootRef.child("Game/Faster")


    suspend fun createGame(whatKindGame: Int, resultList: ArrayList<Int>, gameUid: String, gameTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if ( whatKindGame == 0 || whatKindGame == 1) { //사다리 게임

                val gameid = gameUid
                //결과 : { one : 1 , two : 4 }
                val result = GameResultModel(resultList[0], resultList[1], resultList[2], resultList[3], resultList[4], resultList[5])

                Log.d("abcd","여기까지는 오냐?")
                //게임 모델
                val gameModel = GameModel(gameTitle, auth.displayName!!,gameid,6,6,true,whatKindGame,result)
                ladderRef.child(gameid).setValue(gameModel).addOnSuccessListener {
                    Log.d("abcd","game repo 게임 만들기 성공!!")

                }
            }

        }

    }

    suspend fun getGameList() : LiveData<MutableList<GameModel>> {
        val listData: MutableList<GameModel> = mutableListOf<GameModel>()
        val mutableData = MutableLiveData<MutableList<GameModel>>()

        CoroutineScope(Dispatchers.IO).launch {
            ladderRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(ladders: DataSnapshot, previousChildName: String?) {
                    if (ladders.exists()) {
                        Log.d("abcd","ladders ref is : ${ladders.ref}")
//                        val addedLadderData =
                    }
                }

                override fun onChildChanged(ladders: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(ladders: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(ladders: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        return mutableData

    }
}
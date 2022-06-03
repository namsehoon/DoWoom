package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.activity.game.CreateGameActivity
import com.example.dowoom.model.*
import com.google.firebase.database.*
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

    val resultRef = rootRef.child("Game/Result")

    /** 게임 생성 */
    suspend fun createGame(whatKindGame: Int, resultList: ArrayList<Int>, gameUid: String, gameTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if ( whatKindGame == 0 || whatKindGame == 1) { //사다리 게임

                val gameid = gameUid
                //결과 : { one : 1 , two : 4 }
                val result = GameResultModel(resultList[0], resultList[1], resultList[2], resultList[3], resultList[4], resultList[5])

                Log.d("abcd","여기까지는 오냐?")
                //게임 모델
                val gameModel = GameModel(gameTitle, auth.displayName!!,gameid,0,6,true,whatKindGame,result)
                ladderRef.child(gameid).setValue(gameModel).addOnSuccessListener {
                    Log.d("abcd","game repo 게임 만들기 성공!!")

                }
            }

        }

    }
    /** 게임 리스트 가져오기 */
    suspend fun getGameList() : LiveData<MutableList<GameModel>> {
        val listData: MutableList<GameModel> = mutableListOf<GameModel>()
        val mutableData = MutableLiveData<MutableList<GameModel>>()

        CoroutineScope(Dispatchers.IO).launch {
            ladderRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(ladders: DataSnapshot, previousChildName: String?) {
                    if (ladders.exists()) {
                        val ladderData = ladders.getValue(GameModel::class.java)
                        listData.add(ladderData!!)
                        mutableData.value = listData
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

    /** 이미 끝난 게임 선택 박스 disabled */
    suspend fun observeFinishedGame(gameId: String) : LiveData<Int> {

            val mutableData = MutableLiveData<Int>()

            //이미 다른사용자가 선택한 것은 버튼 선택 할 수 없게 하기
            CoroutineScope(Dispatchers.IO).launch {
                ladderRef.orderByKey().equalTo(gameId).addChildEventListener(object  : ChildEventListener {
                    override fun onChildAdded(choices: DataSnapshot, previousChildName: String?) {
                        if (choices.exists()) {
                            Log.d("abcd", "choices choices is : ${choices.key}")
                            val gameModel = choices.getValue(GameModel::class.java)
                            Log.d("abcd", "gameModel gameModel is : ${gameModel}")
                            mutableData.value = gameModel?.leftCount!!
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        TODO("Not yet implemented")
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }

        return mutableData
    }

    /** 게임 결과 가져오기 */
    suspend fun getGameResult(gameId:String, result:String) : LiveData<String> {
        val mutableData = MutableLiveData<String>()

        CoroutineScope(Dispatchers.IO).launch {
            //게임결과
            ladderRef.child(gameId).child("gameResult").child(result).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(gameResult: DataSnapshot) {
                    val gameResultData = gameResult.getValue()
                    Log.d("abcd","gameresult data(게임 선택에 대한 결과) is : ${gameResultData}")
                    //선물
                    resultRef.child(gameId).child(gameResultData.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(present: DataSnapshot) {
                            Log.d("abcd","present is : ${present.ref}")
                            val gamePresent = present.getValue()
                            if (gamePresent == null) {
                                mutableData.value = "꽝"
                            } else {
                                mutableData.value = gamePresent.toString()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("abcd","error in gameRepo present is : ${error.message}")
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","error in gameRepo result is : ${error.message}")
                }

            })
        }

        return mutableData
    }

    /**  게임 카운트 체크 ! */

     suspend fun checkGameCount() : LiveData<Boolean> {

        val mutableData = MutableLiveData<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            rootRef.child("GameCount").child(auth.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(gameCount: DataSnapshot) {
                    val gameCountData = gameCount.getValue(GameCount::class.java)
                    mutableData.value = gameCountData?.count!! <= 1
                }

                override fun onCancelled(error: DatabaseError) {
                  Log.d("abcd","error checkGameCount in gamerepo  is: ${error}")
                }

            })
        }
        return mutableData
    }

    /** 게임 카운트 차감  */

    suspend fun addGameCount() {
        CoroutineScope(Dispatchers.IO).launch {
            rootRef.child("GameCount").child(auth.uid).child("count").setValue(ServerValue.increment(1))
        }
    }
}
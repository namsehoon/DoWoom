package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.gameModel.GameCount
import com.example.dowoom.model.gameModel.GameModel
import com.example.dowoom.model.gameModel.GameResultModel
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameRepo {


    /** 게임 생성 */
    suspend fun createGame(whatKindGame: Int, resultList: ArrayList<Int>, gameUid: String, gameTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if ( whatKindGame == 0 || whatKindGame == 1) { //사다리 게임

                val gameid = gameUid
                //결과 : { one : 1 , two : 4 }
                val result = GameResultModel(resultList[0], resultList[1], resultList[2], resultList[3], resultList[4], resultList[5])
                val time = System.currentTimeMillis()/1000
                Log.d("abcd","여기까지는 오냐?")
                //게임 모델
                val gameModel = GameModel(gameTitle, Ref().auth.displayName,gameid,6,6,true,whatKindGame,time,result)
                Ref().gameLadderRef().child(gameid).setValue(gameModel).addOnSuccessListener {
                    Log.d("abcd","game repo 게임 만들기 성공!!")

                }
            }

        }

    }
    /** 게임 리스트 가져오기 */
    suspend fun getGameList() : LiveData<MutableList<GameModel>> {
        val listData: MutableList<GameModel> = mutableListOf<GameModel>()

        val mutableData = MutableLiveData<MutableList<GameModel>>()
        val idList:MutableList<String> = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            Ref().gameLadderRef().addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(ladders: DataSnapshot, previousChildName: String?) {
                    if (ladders.exists()) {
                        Log.d("abcd","gamerepo - getGameList - onChildAdded")
                        val ladderData = ladders.getValue(GameModel::class.java)
                        listData.add(0,ladderData!!) //역순
                        idList.add(ladderData.gameUid!!)
                        mutableData.value = listData
                    }
                }

                override fun onChildChanged(ladders: DataSnapshot, previousChildName: String?) {
                    if (ladders.exists()) {
                        Log.d("abcd","gamerepo - getGameList - onChildChanged")
                        Log.d("abcd"," ladders : ${ladders.ref}")

                        val ladderData = ladders.getValue(GameModel::class.java)
                        //게임 아이디 index 찾기
                        val index = idList.indexOf(ladderData?.gameUid!!)
                        if (index > -1) {
                            listData[index] = ladderData
                        }
                        mutableData.value = listData
                    }
                }

                override fun onChildRemoved(ladders: DataSnapshot) {
                    if (ladders.exists()) {
                        Log.d("abcd","gamerepo - getGameList - onChildRemoved")
                        Log.d("abcd"," ladders : ${ladders.ref}")
                    }
                }

                override fun onChildMoved(ladders: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","getGameList - error : "+error.message)
                }
            })
        }
        return mutableData

    }

    /** 사다리 선택 박스 몇개 남았는지 보기 */

    suspend fun observeFinishedGame(gameId: String) : LiveData<GameModel> {

            val mutableData = MutableLiveData<GameModel>()

            //이미 다른사용자가 선택한 것은 버튼 선택 할 수 없게 하기
            CoroutineScope(Dispatchers.IO).launch {
                Ref().gameLadderRef().orderByKey().equalTo(gameId).addChildEventListener(object  : ChildEventListener {
                    override fun onChildAdded(choices: DataSnapshot, previousChildName: String?) {
                        if (choices.exists()) {
                            Log.d("abcd", "gamerepo - observeFinishedGame - onChildAdded")
                            val gameModel = choices.getValue(GameModel::class.java)


                            Log.d("abcd", "gameModel gameModel is : ${gameModel}")
                            mutableData.value = gameModel!!
                        }
                    }

                    override fun onChildChanged(
                        choices: DataSnapshot,
                        previousChildName: String?
                    ) {
                        if (choices.exists()) {
                            Log.d("abcd", "gamerepo - observeFinishedGame - onChildChanged")
                            val gameModel = choices.getValue(GameModel::class.java)
                            mutableData.value = gameModel!!
                        }
                    }

                    override fun onChildRemoved(choices: DataSnapshot) {
                        if (choices.exists()) {
                            Log.d("abcd", "gamerepo - observeFinishedGame - onChildRemoved")
                        }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("abcd", "gamerepo - observeFinishedGame - error is : ${error.message}")
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
            Ref().gameLadderRef().child(gameId).child("gameResult").child(result).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(gameResult: DataSnapshot) {
                    val gameResultData = gameResult.getValue()
                    Log.d("abcd","gameresult data(게임 선택에 대한 결과) is : ${gameResultData}")
                    //선물
                    Ref().gameResultRef().child(gameId).child(gameResultData.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
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
                            Log.d("abcd","getGameResult - 선물 - error  is : ${error.message}")
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","getGameResult - 게임결과 - error is : ${error.message}")
                }

            })
        }

        return mutableData
    }

    /**  게임 카운트 체크 ! */

     suspend fun checkGameCount() : LiveData<Boolean> {

        val mutableData = MutableLiveData<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            Ref().gameCountRef().child(Ref().auth.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(gameCount: DataSnapshot) {
                    if (gameCount.exists()) {
                        val gameCountData = gameCount.getValue(GameCount::class.java)
                        //유저의 게임 count가 1보다 낮으면 true, 아니면 else
                        mutableData.value = gameCountData?.count!! <= 1
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                  Log.d("abcd","error checkGameCount in gamerepo  is: ${error}")
                }

            })
        }
        return mutableData
    }

    /** 게임 카운트 차감  및 게임 leftCount 차감*/

    suspend fun addGameCount(gameuid:String) {
        CoroutineScope(Dispatchers.IO).launch {
            Ref().gameCountRef().child(Ref().auth.uid).child("count").setValue(ServerValue.increment(1))
            Ref().gameLadderRef().child(gameuid).child("leftCount").setValue(ServerValue.increment(-1))
        }
    }

    /** 게임 결과 삭제 및 leftcount 줄이기 -1 */

    suspend fun deleteResult(gameId: String,result: String) : LiveData<Boolean> {
        val mutableData = MutableLiveData<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {

            Ref().gameLadderRef().child(gameId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(game: DataSnapshot) {
                    if (game.exists()) {
                        Log.d("abcd","delte result snapshot.ref is : ${game.ref}")
                        Log.d("abcd","has child is : "+game.hasChild("gameResult/"))
                        val gameData = game.getValue(GameModel::class.java)
                        //아직 남아있는 게임이 있으면 해당 선물만 삭제
                        game.ref.child("gameResult").child(result).setValue(null)
                            .addOnSuccessListener{
                                //성공 시, 게임 데이터 가져와서 leftCount가 0인지 확인
                                mutableData.value = true
                                if (gameData?.leftCount == 0) {
                                    //만약, 0이라면, 게임 없애기
                                    game.ref.removeValue().addOnCompleteListener { Log.d("abcd","게임이 삭제 되었습니다.") }
                                    // 결과 삭제
                                    Ref().gameResultRef().child(gameId).removeValue().addOnCompleteListener { Log.d("abcd","결과가 삭제 되었습니다.") }
                                }
                                Log.d("abcd","게임 삭제완료 ${gameId} , result : ${result}")
                            }
                            .addOnFailureListener { Log.d("abcd","게임 결과 삭제 ERROR : ${it.message}")
                                mutableData.value = false
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd"," deleteResult error is : ${error.message}")
                }
            })
        }
        return mutableData
    }
}
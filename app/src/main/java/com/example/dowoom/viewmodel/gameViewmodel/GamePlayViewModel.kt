package com.example.dowoom.viewmodel.gameViewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.gameModel.GameModel
import com.example.dowoom.repo.GameRepo
import kotlinx.coroutines.launch

class GamePlayViewModel : ViewModel() {

    val repo = GameRepo()


    /** 게임 결과  */

    //chatId 받아오기
    val _img = MutableLiveData<String>()
    val img:LiveData<String> get() = _img

    fun getGameResult(gameId:String, result:String) {
        viewModelScope.launch {
            repo.getGameResult(gameId,result).observeForever(Observer { it ->
                Log.d("abcd","play viewmodel result is : ${it}")
            })
        }
    }

    /** 남은 선택들 개수 */

    fun getLeftCount(gameId:String) : LiveData<GameModel>{
        val game =  MutableLiveData<GameModel>()
        viewModelScope.launch {
            repo.observeFinishedGame(gameId).observeForever(Observer { it ->
                if (it != null) {
                    Log.d("abcd","play viewmodel finished is : ${it}")
                    game.value = it
                }
            })
        }
        return game
    }

    /** 게임 결과 처리 before dismiss activity */

    val _handleDone = MutableLiveData<Boolean>()
    val handleDone:LiveData<Boolean> get() = _handleDone

    fun deleteResult(gameId:String, result:String) {
        viewModelScope.launch {
            repo.deleteResult(gameId,result).observeForever(Observer {
                //만약 처리 안되었으면 dismiss 보류
                if (it != null) {
                    _handleDone.value = it
                }
            })
        }
    }

}
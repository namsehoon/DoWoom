package com.example.dowoom.viewmodel.gameViewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.GameModel
import com.example.dowoom.model.GameResultModel
import com.example.dowoom.model.User
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
                _img.value = it
            })
        }
    }

    /** 남은 선택들 개수 */

    fun getLeftCount(gameId:String) : LiveData<Int>{
        val leftCount =  MutableLiveData<Int>()
        viewModelScope.launch {
            repo.observeFinishedGame(gameId).observeForever(Observer { it ->
                Log.d("abcd","play viewmodel finished is : ${it}")
                leftCount.value = it
            })
        }
        return  leftCount
    }

}
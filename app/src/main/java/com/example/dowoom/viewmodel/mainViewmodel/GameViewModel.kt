package com.example.dowoom.viewmodel.mainViewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.model.gameModel.GameModel
import com.example.dowoom.repo.GameRepo
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    val gameRepo = GameRepo()

    init {
        viewModelScope.launch {
            checkGameCount()
            observeLadderGame()
        }
    }


    /** 게임 리스트 (사다리 게임)*/

    val _gameList = MutableLiveData<MutableList<GameModel>>()
    val gameList : LiveData<MutableList<GameModel>> get() = _gameList

    suspend fun observeLadderGame() {
        gameRepo.getGameList().observeForever(Observer { ladders ->
           if (ladders != null) {
               _gameList.value = ladders
           }
        })

    }

    /** 게임 카운터 체크 ! */

    val _countCheck = MutableLiveData<Boolean>()
    val countCheck : LiveData<Boolean> get() = _countCheck

    suspend fun checkGameCount() : LiveData<Boolean>  {
        viewModelScope.launch {
            gameRepo.checkGameCount().observeForever(Observer { bool ->
                Log.d("abcd","bool is : ${bool}")
                _countCheck.value = bool
            })
        }
        return countCheck

    }

    fun addGameCount(gameuid:String) {
        viewModelScope.launch {
            gameRepo.addGameCount(gameuid)
        }
    }

}
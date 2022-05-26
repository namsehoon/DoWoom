package com.example.dowoom.viewmodel.mainViewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.model.GameModel
import com.example.dowoom.model.Message
import com.example.dowoom.repo.GameRepo
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    val gameRepo = GameRepo()



    /** 게임 리스트 (사다리 게임)*/

    val _gameModel = MutableLiveData<MutableList<GameModel>>()
    val gameModel : LiveData<MutableList<GameModel>> get() = _gameModel

    suspend fun observeLadderGame(): LiveData<MutableList<GameModel>> {
        gameRepo.getGameList().observeForever(Observer { ladders ->
            _gameModel.value = ladders
        })

        return gameModel
    }

}
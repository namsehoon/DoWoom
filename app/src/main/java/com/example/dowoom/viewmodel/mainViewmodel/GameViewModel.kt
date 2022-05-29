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

    suspend fun observeLadderGame(): LiveData<MutableList<GameModel>> {
        val gameList = MutableLiveData<MutableList<GameModel>>()
        gameRepo.getGameList().observeForever(Observer { ladders ->
            gameList.value = ladders
        })

        return gameList
    }

}
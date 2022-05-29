package com.example.dowoom.viewmodel.gameViewmodel

import androidx.lifecycle.*
import com.example.dowoom.model.User
import com.example.dowoom.repo.GameRepo
import kotlinx.coroutines.launch

class GamePlayViewModel : ViewModel() {

    val repo = GameRepo()

    //chatId 받아오기
    val _img = MutableLiveData<String>()
    val img:LiveData<String> get() = _img


    fun getGameResult(gameId:String, result:String) {
        viewModelScope.launch {
            repo.getGameResult(gameId,result).observeForever(Observer { it ->
                _img.value = it
            })
        }
    }

}
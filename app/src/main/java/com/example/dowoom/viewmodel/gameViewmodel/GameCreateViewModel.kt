package com.example.dowoom.viewmodel.gameViewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.repo.GameRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameCreateViewModel : ViewModel() {

    val repo = GameRepo()
    val etGameTitle = MutableLiveData<String>("")

    val _created = MutableLiveData<Boolean>()
    val created: LiveData<Boolean> get() = _created

    suspend fun createGame(whatKindGame:Int, resultList: ArrayList<Int>, gameUid:String) {
        viewModelScope.launch {
            if (!etGameTitle.value.toString().isNullOrEmpty()) {
                repo.createGame(whatKindGame,resultList, gameUid, etGameTitle.value.toString()).observeForever(Observer {
                        _created.value = it
                })

            }
        }
    }


}
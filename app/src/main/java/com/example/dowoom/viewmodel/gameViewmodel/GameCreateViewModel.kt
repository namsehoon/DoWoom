package com.example.dowoom.viewmodel.gameViewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.repo.GameRepo
import kotlinx.coroutines.launch

class GameCreateViewModel : ViewModel() {

    val repo = GameRepo()
    val etGameTitle = MutableLiveData<String>("")

    suspend fun createGame(whatKindGame:Int, resultList: ArrayList<Int>, gameUid:String) {
        viewModelScope.launch {
            var gameTitle = ""
            if (etGameTitle.value.toString().isNullOrEmpty()) {
                gameTitle = "어서들 오시게~"
                repo.createGame(whatKindGame,resultList, gameUid, gameTitle)
                Log.d("abcd","실행됨 1")
                onEndLive.value = true
                Log.d("abcd","실행됨 2")
            } else {
                repo.createGame(whatKindGame,resultList, gameUid, etGameTitle.value.toString())
                Log.d("abcd","실행됨 1")
                onEndLive.value = true
                Log.d("abcd","실행됨 2")
            }
        }
    }

    private val onEndLive = MutableLiveData<Boolean>()

    fun getOnEndLive(): MutableLiveData<Boolean> {
        return onEndLive
    }

    fun somethingHappens() {
        onEndLive.value = true
    }

}
package com.example.dowoom.viewmodel.chatviewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.talkModel.Message
import com.example.dowoom.repo.ChatRepo
import kotlinx.coroutines.*
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.flow.*


class ChatViewmodel : ViewModel() {


    private val chatRepo = ChatRepo()
    private val userRepo = com.example.dowoom.repo.userRepo()
    //etMessage
    val etMessage = MutableLiveData<String>("")


    /** 메세지 보내기 (채팅룸 업데이트)*/

    val _message = MutableLiveData<Message>()
    val message : LiveData<Message> get() = _message

    suspend fun insertMessage(message:String, from:String, to: String)  {
        viewModelScope.launch {
            //메세지 보내기
            chatRepo.sendMessage(from, to, message)
        }

    }

    /** 차단 관찰 */
    val _blockCheck = MutableLiveData<Boolean>()
    val blockCheck : LiveData<Boolean> get() = _blockCheck

    fun observeBlock(partnerUid: String) {
        userRepo.observeBlock(partnerUid).observeForever(Observer {
            _blockCheck.value = it
        })
    }


}



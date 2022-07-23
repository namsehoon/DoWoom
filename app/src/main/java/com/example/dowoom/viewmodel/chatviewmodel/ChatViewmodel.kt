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


}



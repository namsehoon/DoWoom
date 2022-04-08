package com.example.dowoom.viewmodel.chatviewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Message
import com.example.dowoom.model.User
import com.example.dowoom.repo.ChatRepo
import com.example.dowoom.repo.repo
import com.example.dowoom.viewmodel.SingleLiveEvent
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.launch

class ChatViewmodel : ViewModel() {
    private val chatRepo = ChatRepo()

    //etMessage
    val etMessage = MutableLiveData<String>("")

    suspend fun insertMessage(chatId: String,message:String,sender:String,otherUid: String) {
        viewModelScope.launch {
            //메세지 insert
            val time = System.currentTimeMillis()/1000
            chatRepo.insertMessage(chatId, sender, otherUid, null, message, time)

        }
    }


    suspend fun observeMessage(otherUid: String, chatId: String): LiveData<MutableList<Message>> {
        val messages = MutableLiveData<MutableList<Message>>()

        chatRepo.getMessageData(otherUid, chatId).observeForever(Observer { it ->
            messages.value = it
        })
        return messages
    }
}



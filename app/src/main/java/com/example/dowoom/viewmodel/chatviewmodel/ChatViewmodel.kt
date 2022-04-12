package com.example.dowoom.viewmodel.chatviewmodel

import androidx.lifecycle.*
import com.example.dowoom.model.Message
import com.example.dowoom.repo.ChatRepo
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

    suspend fun deleteMessage(
        chatId: String,
        messageId: String,
        otherUid: String,
        timeStamp: Long,
        sender: String
    ) {
        viewModelScope.launch {
            chatRepo.deleteMessage(chatId,messageId,otherUid,timeStamp,sender)
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



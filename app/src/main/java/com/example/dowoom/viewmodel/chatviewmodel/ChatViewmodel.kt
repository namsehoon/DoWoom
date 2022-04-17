package com.example.dowoom.viewmodel.chatviewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.Message
import com.example.dowoom.repo.ChatRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewmodel : ViewModel() {
    private val chatRepo = ChatRepo()

    //etMessage
    val etMessage = MutableLiveData<String>("")

    val _message = MutableLiveData<Message>()
    val message : LiveData<Message> get() = _message

    //channelFlow : 쓰레드 안전, cold(동기), 파이프라인,
    suspend fun insertMessage(chatId: String, message:String, sender:String, otherUid: String)  {
        viewModelScope.launch {
            //메세지 insert
            val time = System.currentTimeMillis()/1000
            chatRepo.insertMessage(chatId, sender, otherUid, null, message, time)
                .catch { error ->
                    Log.d("abcd","insert message error is :${error.message}") }
                .collect { result ->
                    _message.value = result
                }

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



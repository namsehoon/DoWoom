package com.example.dowoom.viewmodel.chatviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Message
import com.example.dowoom.model.User
import com.example.dowoom.repo.ChatRepo

class ChatViewmodel : ViewModel() {
    private val chatRepo = ChatRepo()

    //etMessage
    val etMessage = MutableLiveData<String>("")




    suspend fun observeMessage(otherUid:String, chatId:String): LiveData<MutableList<Message>> {
        val messages = MutableLiveData<MutableList<Message>>()
        chatRepo.getMessageData(otherUid,chatId).observeForever(Observer { it ->
            messages.value = it
        })
        return messages
    }



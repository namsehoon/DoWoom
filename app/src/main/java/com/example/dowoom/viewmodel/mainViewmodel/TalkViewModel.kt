package com.example.dowoom.viewmodel.mainViewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.repo.ChatRepo

class TalkViewModel : ViewModel() {

    private val chatRepo = ChatRepo()

    suspend fun observeChat(): LiveData<MutableList<ChatRoom>> {
        val chatList = MutableLiveData<MutableList<ChatRoom>>()
        chatRepo.getChatData().observeForever(Observer { it ->
            chatList.value = it
        })

        return chatList

    }
}
package com.example.dowoom.viewmodel.mainViewmodel

import androidx.lifecycle.*
import com.example.dowoom.model.talkModel.ChatRoom
import com.example.dowoom.model.talkModel.Member
import com.example.dowoom.repo.ChatRepo
import kotlinx.coroutines.launch

class TalkViewModel : ViewModel() {

    private val chatRepo = ChatRepo()

    /** 채팅룸 전체 관찰*/

    suspend fun observeChat(): LiveData<MutableList<ChatRoom>> {
        val chatList = MutableLiveData<MutableList<ChatRoom>>()
            chatRepo.getChatRoomData().observeForever(Observer { it ->
                chatList.value = it
            })

        return chatList

    }

    /** 채팅룸 삭제*/

    suspend fun deleteChatRoom(chatId: String, member: Member) {
        viewModelScope.launch {
            chatRepo.deleteChatRoom(chatId, member)
        }
    }
}
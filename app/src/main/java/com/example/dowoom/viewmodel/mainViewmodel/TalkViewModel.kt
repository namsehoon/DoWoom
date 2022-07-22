package com.example.dowoom.viewmodel.mainViewmodel

import androidx.lifecycle.*
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.talkModel.ChatRoom
import com.example.dowoom.model.talkModel.Member
import com.example.dowoom.repo.ChatRepo
import kotlinx.coroutines.launch

class TalkViewModel : ViewModel() {

    private val chatRepo = ChatRepo()

    init {
        viewModelScope.launch {
            observeChat(Ref().auth.uid)
        }
    }

    val _observeChat = MutableLiveData<MutableList<ChatRoom>>()
    val observeChat:LiveData<MutableList<ChatRoom>> get() = _observeChat

    /** 채팅룸 전체 관찰*/
    suspend fun observeChat(uid:String) {
        chatRepo.getChatRoomData(uid).observeForever(Observer { it ->
            _observeChat.value = it
        })
    }

    /** 채팅룸 삭제*/

//    suspend fun deleteChatRoom(chatId: String, member: Member) {
//        viewModelScope.launch {
//            chatRepo.deleteChatRoom(chatId, member)
//        }
//    }
}
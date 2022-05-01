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

    //    suspend fun insertMessage(ImageUri:String?, message:String, sender:String, otherUid: String, otherNickname:String, chatId:String)  {
//        viewModelScope.launch {
//            //메세지 insert
//            val time = System.currentTimeMillis()/1000
//            chatRepo.insertMessage(ImageUri,sender, otherUid, message, time, otherNickname,chatId)
//                .catch { error ->
//                    Log.d("abcd","insert message error is :${error.message}") }
//                .collect { result ->
//                    _message.value = result
//                }
//
//        }
//    }

    val _message = MutableLiveData<Message>()
    val message : LiveData<Message> get() = _message

    /** 메세지 추가 (채팅룸 업데이트)*/
    suspend fun insertMessage(ImageUri:String?, message:String, sender:String, otherUid: String, otherNickname:String, chatId:String)  {
        viewModelScope.launch {
            //메세지 insert
            val time = System.currentTimeMillis()/1000
            chatRepo.insertMessage(ImageUri,sender, otherUid, message, time, otherNickname,chatId).observeForever(
                Observer { result ->
                    _message.value = result
            })

        }
    }

    /** 메세지 삭제 */
    suspend fun deleteMessage(
        messageId: String,
        otherUid: String,
        timeStamp: Long,
        sender: String,
        chatId: String
    ) {
        viewModelScope.launch {
            chatRepo.deleteMessage(messageId,otherUid,timeStamp,sender,chatId)
        }
    }

    /** 전체 메세지 관찰 */
    suspend fun observeMessage(chatId:String): LiveData<MutableList<Message>> {
        val messages = MutableLiveData<MutableList<Message>>()

        chatRepo.getMessageData(chatId).observeForever(Observer { it ->
            messages.value = it
        })
        return messages
    }
}



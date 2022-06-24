package com.example.dowoom.viewmodel.chatviewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.talkModel.Message
import com.example.dowoom.repo.ChatRepo
import kotlinx.coroutines.*

class ChatViewmodel : ViewModel() {


    private val chatRepo = ChatRepo()


    //etMessage
    val etMessage = MutableLiveData<String>("")

    /** 만약 채팅 멤버 한명만 null이면, 메세지 작성 및 버튼 비활성화 */

    val _memberCheck = MutableLiveData<Boolean>()
    val memberCheck : LiveData<Boolean> get() = _memberCheck

    suspend fun checkChatRoomMemberOneNull() {
        chatRepo.memberResult.observeForever(Observer {
            _memberCheck.value = it
        })
    }

    /** chatid 가져오기 */

    val _chatId = MutableLiveData<String>()
    val chatId : LiveData<String> get() = _chatId

    suspend fun getChatId(otherUid: String)  {
        viewModelScope.launch {
            chatRepo.getChatIdRepo(otherUid).observeForever(Observer { id ->
                _chatId.value = id
            })
        }
    }



    /** 메세지 추가 (채팅룸 업데이트)*/

    val _message = MutableLiveData<Message>()
    val message : LiveData<Message> get() = _message

    suspend fun insertMessage(ImageUri:String?, message:String, sender:String, otherUid: String, otherNickname:String)  {
        viewModelScope.launch {
            //메세지 insert
            val time = System.currentTimeMillis()/1000
            if (!chatId.value.toString().isNullOrEmpty()) {
                val chat = chatId.value.toString()
                Log.d("abcd","chatid is in viewmodel insertmessage() : ${chat}")
                chatRepo.insertMessage(ImageUri,sender, otherUid, message, time, otherNickname,chat).observeForever(
                    Observer { result ->
                        _message.value = result
                    })
            }

        }
    }

    /** 메세지 삭제 */
    suspend fun deleteMessage(
        messageId: String
    ) {
        viewModelScope.launch {
            if (chatId.value.toString() != null) {
                chatRepo.deleteMessage(messageId,chatId.value.toString())
            }
        }
    }


    /** 전체 메세지 관찰 */
    suspend fun observeMessage(): LiveData<MutableList<Message>> {
        val messages = MutableLiveData<MutableList<Message>>()
        delay(500)
        if (!chatId.value.toString().isNullOrEmpty()) {
            val chat = chatId.value.toString()

            Log.d("abcd","chatid is in getmessagedata is  22 : ${chat} ")

            chatRepo.getMessageData(chat).observeForever(Observer { it ->
                messages.value = it
            })
        }
        return messages
    }
}



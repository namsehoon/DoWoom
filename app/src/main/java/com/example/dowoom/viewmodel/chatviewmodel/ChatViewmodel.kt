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

    /** 만약 채팅 멤버 한명만 null이면, 메세지 작성 및 버튼 비활성화 */

    val _memberCheck = MutableLiveData<Boolean>()
    val memberCheck : LiveData<Boolean> get() = _memberCheck

    suspend fun checkChatRoomMemberOneNull() {
        chatRepo.memberResult.observeForever(Observer {
            _memberCheck.value = it
        })
    }


    /** 메세지 보내기 (채팅룸 업데이트)*/

    val _message = MutableLiveData<Message>()
    val message : LiveData<Message> get() = _message

    suspend fun insertMessage(imageUrl:String?, message:String, from:String, to: String)  {
        viewModelScope.launch {
            //메세지 보내기
            chatRepo.sendMessage(imageUrl,from, to, message)
        }

    }




    /** 메세지 삭제 */
//    suspend fun deleteMessage(
//        messageId: String
//    ) {
//        viewModelScope.launch {
//            if (chatId.value.toString() != null) {
//                chatRepo.deleteMessage(messageId,chatId.value.toString())
//            }
//        }
//    }


    fun <T> merge(vararg flows: Flow<T>): Flow<T> = channelFlow {
        val flowJobs = flows.map { flow ->
            GlobalScope.launch { flow.collect { send(it) } }
        }
        flowJobs.joinAll()
    }

    /** 전체 메세지 관찰 */
    @ExperimentalCoroutinesApi
    suspend fun observeMessage(from: String, to: String) : Flow<Message> {

        val ff = flow<Message> {
            chatRepo.observeMessage(from, to).asFlow().collect {
                emit(it)
            }
        }
        val gg = flow<Message> {
            chatRepo.observeMessage(to, from).asFlow().collect {
                emit(it)
            }
        }
        val together: Flow<Message> = merge(ff,gg)

        return flow {

            together.collect { it ->
                Log.d("abcd","it is :${it}")
                emit(it)
            }
        }
    }

}



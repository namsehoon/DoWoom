package com.example.dowoom.viewmodel.chatviewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.Message
import com.example.dowoom.model.UserChat
import com.example.dowoom.repo.ChatRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.days

class ChatViewmodel : ViewModel() {
    private val chatRepo = ChatRepo()

    val db = FirebaseDatabase.getInstance().reference.database

    //etMessage
    val etMessage = MutableLiveData<String>("")

    /** chatid 가져오기 */

    val _chatId = MutableLiveData<String>()
    val chatId : LiveData<String> get() = _chatId

    suspend fun getChatId(otherUid: String) : LiveData<String> {

        val myuid = FirebaseAuth.getInstance().currentUser?.uid


            db.reference.child("UserChat").child(myuid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("abcd","snapshot is ; ${snapshot.ref}")
                        for (child in snapshot.children) {

                            val getChatid = child.getValue(UserChat::class.java)

                            //만약 "내"가 제공한 내 uid를 사용하여 "상대방"의 uid가 같다면, chatid 리턴
                            if (getChatid?.otherUid == otherUid) {
                                _chatId.value = getChatid.chatId!!
                                Log.d("abcd","get chatid in viewmodel getChatid() is : ${getChatid.chatId}")
                            }
                        }
                    } else {
                        Log.d("abcd","snapshot doesnt exist in ChatViewModel ")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","error in chatviewmodel is :${error.message}")
                }

            })


        return chatId
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
    suspend fun observeMessage(): LiveData<MutableList<Message>> {
        val messages = MutableLiveData<MutableList<Message>>()

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



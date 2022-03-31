package com.example.dowoom.repo

import android.util.Log
import com.example.dowoom.model.ChatMember
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Message
import com.example.dowoom.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ChatRepo : repo{


    //채팅 fragment
    val talkRef = rootRef.child("Talks")
    //채팅방 내 메세지
    val messageRef = rootRef.child("Messages")
    //채팅방 내 유저
    val memberRef = rootRef.child("Members")

    /** insert new Chat */ // todo fromUid: 나, toUid : 상대방
    suspend fun insertNewChat(nickname:String,toUid:String) {
        withContext(Dispatchers.IO) {

            val pushChatPush = talkRef.push()
            val chatId = pushChatPush.key
            val messagePush = messageRef.push()
            val messageId = messagePush.key
            val memberPush = memberRef.push()
            val memberId = memberPush.key

            val chatRoom = ChatRoom(chatId, nickname, null, null, 0, false)
            val message = Message(chatId, messageId, null, null, 0, false)
            val chatMember = ChatMember(chatId, toUid, auth.uid, nickname)

            pushChatPush.child(chatId!!).setValue(chatRoom)
                .addOnCompleteListener { Log.d("abcd", "chat 성공") }
                .addOnFailureListener { Log.d("abcd", "chat 실패") }
            messagePush.child(chatId).child(messageId!!).setValue(message)
                .addOnCompleteListener { Log.d("abcd", "message 성공") }
                .addOnFailureListener { Log.d("abcd", "message 실패 ") }
            memberPush.child(chatId).setValue(chatMember)
                .addOnCompleteListener { Log.d("abcd", "member 성공") }
                .addOnFailureListener { Log.d("abcd", "member 실패") }

        }
    }

}
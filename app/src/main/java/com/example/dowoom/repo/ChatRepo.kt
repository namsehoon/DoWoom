package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.lang.reflect.Member

class ChatRepo : repo{


    //채팅 fragment
    val talkRef = rootRef.child("ChatRoom")
    //채팅방 내 메세지
    val messageRef = rootRef.child("Message")
    //채팅방 내 유저
    val memberRef = rootRef.child("Member")

    suspend fun getMessageData() : LiveData<MutableList<Message>> {
        val mutableData = MutableLiveData<MutableList<Message>>()
        val listData: MutableList<Message> = mutableListOf<Message>()



        return mutableData
    }


    suspend fun getChatRoomData() : LiveData<MutableList<ChatRoom>> {

        val mutableData = MutableLiveData<MutableList<ChatRoom>>()
        val listData: MutableList<ChatRoom> = mutableListOf<ChatRoom>()

        val myUid = auth.uid

        //내 대화룸
        talkRef.orderByChild(myUid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(chatRooms: DataSnapshot, previousChildName: String?) {
                if (chatRooms.exists()) {
                    for(chatRoom in chatRooms.children) {
                        val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
                        listData.add(getChatRoom!!)
                    }
                    mutableData.value = listData
                } else {
                    Log.d("abcd","chatRooms이 없음 ")
                }
            }

            override fun onChildChanged(chatRooms: DataSnapshot, previousChildName: String?) {
                if (chatRooms.exists()) {
                    for(chatRoom in chatRooms.children) {
                        val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
                        //todo : lastmessage 및 timpstamp가 변경될 수 있음
                        listData.add(getChatRoom!!)
                    }
                    mutableData.value = listData
                } else {
                    Log.d("abcd","chatRooms이 없음 ")
                }
            }

            override fun onChildRemoved(chatRooms: DataSnapshot) {
                Log.d("abcd","chatRooms : "+chatRooms.value)
            }

            override fun onChildMoved(chatRooms: DataSnapshot, previousChildName: String?) {
                Log.d("abcd","chatRooms : "+chatRooms.value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("abcd","error in ChatRepo at getChatData : "+error.message)
            }

        })

        return mutableData

    }


    /** insert new Chat */ // todo fromUid: 나, toUid : 상대방
    suspend fun checkedChat(user: User) {

        withContext(Dispatchers.IO) {
            val otherUid = user.uid
            val myUid = auth.uid

            Log.d("abcd","상대방 uid : "+otherUid)
            Log.d("abcd","내 uid : "+myUid)

            memberRef.child(myUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(members: DataSnapshot) {
                    Log.d("abcd","snapshot is : "+members.value)

                    if (members.exists()) {
                        for (member in members.children) {
                            Log.d("abcd","member.value is : "+member.value)
                            val getMember = member.getValue(com.example.dowoom.model.Member::class.java)
                            //똑같은 멤버로 구성된 채팅방이 있는지 보기
                            if(getMember?.otherUid == otherUid && getMember?.myUid == myUid) {
                                //todo : 있는 곳으로 가야 됨
                                Log.d("abcd","other 같음 : "+ getMember?.otherUid +" : "+ otherUid)
                                Log.d("abcd","myuid 같음 : "+ getMember?.myUid +" : "+ myUid)
                            } else {
                                //똑같은 멤버로 구성된 채팅방이 없음
                                Log.d("abcd","other 다름 : "+ getMember?.otherUid +" : "+ otherUid)
                                Log.d("abcd","myuid 다름 : "+ getMember?.myUid +" : "+ myUid)
                                val pushChatPush = talkRef.push()
                                val chatId = pushChatPush.key
                                val messageId = messageRef.push().key

                                val chatRoom = ChatRoom(chatId,otherUid ,user.nickname, null, 0, false)
                                val message = Message(chatId, messageId, null, null, 0, false)
                                val chatMember = com.example.dowoom.model.Member(chatId,myUid, user.nickname, otherUid)


                                talkRef.child(myUid).child(chatId!!).setValue(chatRoom)
                                    .addOnCompleteListener { Log.d("abcd", "chat 성공") }
                                    .addOnFailureListener { Log.d("abcd", "chat 실패") }
                                messageRef.child(myUid).child(chatId).child(messageId!!).setValue(message)
                                    .addOnCompleteListener { Log.d("abcd", "message 성공") }
                                    .addOnFailureListener { Log.d("abcd", "message 실패 ") }
                                memberRef.child(myUid).child(chatId).setValue(chatMember)
                                    .addOnCompleteListener { Log.d("abcd", "member 성공") }
                                    .addOnFailureListener { Log.d("abcd", "member 실패") }

                            }
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","fuck away from me in ChatRepo : "+error.message)
                }


            })
        }

    }

    /** 이미 */

}
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.reflect.Member

class ChatRepo : repo{


    //채팅 fragment
    val talkRef = rootRef.child("ChatRoom")
    //채팅방 내 메세지
    val messageRef = rootRef.child("Message")
    //채팅방 내 유저
    val memberRef = rootRef.child("Member")

    suspend fun insertMessage(chatId: String,sender:String,otherUid: String,imageUrl:String? = null,newMessage:String? = null,timestamp:Long) {

        CoroutineScope(Dispatchers.IO).launch {
            //삭제할 때 필요함
            val messageId = messageRef.push().key
            val message = Message(chatId,sender,otherUid,imageUrl,newMessage, messageId,timestamp,false)

            messageRef.child(auth.uid).child(chatId).child(messageId!!).setValue(message)
                .addOnCompleteListener { Log.d("Abcd","메세지 보내기 성공 : ${messageId} ") }
                .addOnFailureListener { Log.d("Abcd","메세지 보내기 실패 : ${messageId} ") }
        }
    }

    //개인 채팅방
    suspend fun getMessageData(otherUid:String,chatId:String) : LiveData<MutableList<Message>> {

        val listData: MutableList<Message> = mutableListOf<Message>()
        val mutableData = MutableLiveData<MutableList<Message>>()



        //대화방 메시지 -> 각 대화방의 멤버를 찾아서 넣어줌
        messageRef.child(auth.uid).orderByChild(chatId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(messages: DataSnapshot, previousChildName: String?) {
                if (messages.exists()) {
                    for (message in messages.children) {
                        val getData = message.getValue(Message::class.java)
                        //멤버의 상대방과 메세지의 상대방이 일치할 시,
                        Log.d("abcd","otherUid is : ${otherUid} and getData otheruid is : ${getData?.otherUid}")
                        if (otherUid == getData?.otherUid) {
                            listData.add(getData)
                        } else {
                            Log.d("abcd","일치하지 않습니다 in ChatRepo")
                        }
                    }
                    mutableData.value = listData

                }
            }

            override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                if (messages.exists()) {
                    Log.d("abcd","messsages changed is ${messages.ref}")
                }


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("abcd","snapshot is : "+snapshot)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("abcd","snapshot is : "+snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("abcd","error in getMessage  is : "+error.message)
            }

        })

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

            val pushChatPush = talkRef.push()
            val chatId = pushChatPush.key
            val messageId = messageRef.push().key


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

                                val chatRoom = ChatRoom(chatId,otherUid ,user.nickname, null, 0, false)
                                val message = Message(chatId, myUid, otherUid, null, null,messageId,0, false)
                                val chatMember = com.example.dowoom.model.Member(chatId,myUid, user.nickname, otherUid)

                                talkRef.child(myUid).child(chatId!!).setValue(chatRoom)
                                    .addOnCompleteListener { Log.d("abcd", "chat 성공") }
                                    .addOnFailureListener { Log.d("abcd", "chat 실패") }
                                memberRef.child(myUid).child(chatId).setValue(chatMember)
                                    .addOnCompleteListener { Log.d("abcd", "member 성공") }
                                    .addOnFailureListener { Log.d("abcd", "member 실패") }
                                messageRef.child(myUid).child(chatId).child(messageId!!).setValue(message)


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
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
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.reflect.Member
import java.util.*
import kotlin.collections.HashMap

class ChatRepo : repo{


    //채팅 fragment
    val talkRef = rootRef.child("ChatRoom")
    //채팅방 내 메세지
    val messageRef = rootRef.child("Message")
    //채팅방 내 유저
    val memberRef = rootRef.child("Member")

    /** 메세지 삭제 */
    suspend fun deleteMessage(messageId: String, otherUid: String, timeStamp: Long, sender: String) {
        //chatid 구하기
        CoroutineScope(Dispatchers.IO).launch {

            val message = Message(sender,otherUid,null,"삭제되었습니다.",messageId,timeStamp,true)
            val messageValue = message.toMap()
            val childUpdates = hashMapOf<String,Any>(
                "/Message/$sender/$otherUid/$messageId" to messageValue
            )
            database.reference.updateChildren(childUpdates)
                .addOnCompleteListener { Log.d("abcd","메세지 삭제 성공.") }
                .addOnFailureListener { Log.d("abcd","메세지 삭제 실패.") }
        }
    }

    /** 메세지 추가 */
    suspend fun insertMessage(ImageUri:String? = null, sender:String,otherUid: String, newMessage:String? = null,timestamp:Long, otherNickname:String) : Flow<Message> {

        return flow<Message> {
            //삭제할 때 필요함
            val messageId = messageRef.push().key
            val message:Message

            if (newMessage == "photo" && ImageUri !== null) {
                //사진 보내기
                message = Message(sender,otherUid,ImageUri,"photo", messageId,timestamp,false)
            } else {
                //메세지만 보내기
                message = Message(sender,otherUid,null,newMessage, messageId,timestamp,false)
            }

            messageRef.child(auth.uid).child(otherUid).child(messageId!!).setValue(message)
                .addOnCompleteListener {
                    Log.d("Abcd","메세지 보내기 성공 : ${messageId} ")
                    //last message 업데이트 todo : 이게 최선인가?..
                    updateChatRoom(sender,newMessage!!,otherUid,timestamp,otherNickname)
                }
                .addOnFailureListener { Log.d("Abcd","메세지 보내기 실패 : ${messageId} ") }

            emit(message)
        }.flowOn(Dispatchers.IO)
    }

    /** 채팅룸 업데이트 : lastmessage 및 timeStamp */
    fun updateChatRoom(sender:String,lastMessage:String, otherUid: String, timestamp: Long, otherNickname: String) {

        var profileImg:StorageReference? =  storage.reference.child("User/$otherUid/profileImages/${otherUid.plus(".jpeg")}")
        Log.d("abcd","profileimg path is : ${profileImg}")

        val chatroom = ChatRoom(profileImg?.path,otherUid,otherNickname,lastMessage,timestamp,false)
        val chatRoomValue = chatroom.toMap()
        val chatRoomUpdate = hashMapOf<String,Any>(
            "ChatRoom/$sender/$otherUid" to chatRoomValue
        )

        database.reference.updateChildren(chatRoomUpdate)
            .addOnCompleteListener {
                Log.d("abcd","채팅룸 업데이트 성공.")
            }
            .addOnFailureListener { Log.d("abcd","채팅룸 업데이트 실패.") }


    }

    /** 내 채팅방내 메세지 가져오기 */
    suspend fun getMessageData(otherUid:String) : LiveData<MutableList<Message>> {

        val listData: MutableList<Message> = mutableListOf<Message>()
        val mutableData = MutableLiveData<MutableList<Message>>()

        //대화방 메시지 -> 각 대화방의 멤버를 찾아서 넣어줌
        messageRef.child(auth.uid).orderByChild(otherUid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(messages: DataSnapshot, previousChildName: String?) {
                if (messages.exists()) {
                    Log.d("abcd", "messages.ref is ${messages.ref}")
                    for (message in messages.children) {
                        val getData = message.getValue(Message::class.java)
                        //멤버의 상대방과 메세지의 상대방이 일치할 시,
                        if (otherUid == getData?.otherUid) {
                            listData.add(getData)
                        } else {
                            Log.d("abcd","일치하지 않습니다 in ChatRepo")
                        }
                    }
                    mutableData.value = listData

                } else {
                    Log.d("abcd","load할 메세지가 없습니다.")
                }
            }
            //메세지가 새로 추가되거나, 메세지 삭제버튼을 눌러서 "message text 변경"
            override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                if (messages.exists()) {
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

    /** 내 채팅방 가져오기 */
    suspend fun getChatRoomData() : LiveData<MutableList<ChatRoom>> {

        val mutableData = MutableLiveData<MutableList<ChatRoom>>()
        val listData: MutableList<ChatRoom> = mutableListOf<ChatRoom>()

        val myUid = auth.uid

        //내 대화룸
        talkRef.orderByKey().equalTo(myUid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(chatRooms: DataSnapshot, previousChildName: String?) {
                if (chatRooms.exists()) {
                    listData.clear()
                    Log.d("abcd","chat room is : ${chatRooms.ref}")
                    for(chatRoom in chatRooms.children) {
                        val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
                        listData.add(getChatRoom!!)
                    }
                    mutableData.value = listData
                } else {
                    Log.d("abcd","chatRooms이 없음 ")
                }
            }
            // /users/<userId>/images/<image-file>
            override fun onChildChanged(chatRooms: DataSnapshot, previousChildName: String?) {
                if (chatRooms.exists()) {
                    if (chatRooms.exists()) {
                        listData.clear()
                        Log.d("abcd","chat room is : ${chatRooms.ref}")
                        for(chatRoom in chatRooms.children) {
                            val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
                            listData.add(getChatRoom!!)
                        }
                        mutableData.value = listData
                    } else {
                        Log.d("abcd","chatRooms이 없음 ")
                    }

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

    /** 채팅 시작 */
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
                                Log.d("abcd","other 같음 : "+ getMember.otherUid +" : "+ otherUid)
                                Log.d("abcd","myuid 같음 : "+ getMember.myUid +" : "+ myUid)
                            } else { //새로운 채팅방
                                val messageId = messageRef.push().key

                                val chatRoom = ChatRoom(user.profileImg,otherUid ,user.nickname, null, 0, false)
                                val message = Message( myUid, otherUid, null, null,messageId,0, false)
                                val chatMember = com.example.dowoom.model.Member(myUid, user.nickname, otherUid)

                                talkRef.child(myUid).child(otherUid!!).setValue(chatRoom)
                                    .addOnCompleteListener { Log.d("abcd", "chat 성공") }
                                    .addOnFailureListener { Log.d("abcd", "chat 실패") }
                                memberRef.child(myUid).child(otherUid).setValue(chatMember)
                                    .addOnCompleteListener { Log.d("abcd", "member 성공") }
                                    .addOnFailureListener { Log.d("abcd", "member 실패") }
                                messageRef.child(myUid).child(otherUid).child(messageId!!).setValue(message)

                            }
                        }
                    } else {
                        val messageId = messageRef.push().key

                        val chatRoom = ChatRoom(user.profileImg, otherUid ,user.nickname, null, 0, false)
                        val message = Message( myUid, otherUid, null, null,messageId,0, false)
                        val chatMember = com.example.dowoom.model.Member(myUid, user.nickname, otherUid)

                        talkRef.child(myUid).child(otherUid!!).setValue(chatRoom)
                            .addOnCompleteListener { Log.d("abcd", "chat 성공") }
                            .addOnFailureListener { Log.d("abcd", "chat 실패") }
                        memberRef.child(myUid).child(otherUid).setValue(chatMember)
                            .addOnCompleteListener { Log.d("abcd", "member 성공") }
                            .addOnFailureListener { Log.d("abcd", "member 실패") }
                        messageRef.child(myUid).child(otherUid).child(messageId!!).setValue(message)

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","fuck away from me in ChatRepo : "+error.message)
                }


            })

        }
    }



}
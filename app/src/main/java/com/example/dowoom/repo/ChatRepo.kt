package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Message
import com.example.dowoom.model.User
import com.example.dowoom.model.UserChat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.lang.reflect.Member
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatRepo : repo {


    //채팅 fragment
    val talkRef = rootRef.child("ChatRoom")

    //채팅방 내 메세지
    val messageRef = rootRef.child("Message")

    //채팅방 내 유저
    val memberRef = rootRef.child("Member")

    //chatId
    val userChatRef = rootRef.child("UserChat")

    /** 메세지 삭제 */
    suspend fun deleteMessage(
        messageId: String,
        otherUid: String,
        timeStamp: Long,
        sender: String
    ) {
        //chatid 구하기
        CoroutineScope(Dispatchers.IO).launch {

            val message = Message(sender, otherUid, null, "삭제되었습니다.", messageId, timeStamp, true)
            val messageValue = message.toMap()
            val childUpdates = hashMapOf<String, Any>(
                "/Message/$sender/$otherUid/$messageId" to messageValue
            )
            database.reference.updateChildren(childUpdates)
                .addOnCompleteListener { Log.d("abcd", "메세지 삭제 성공.") }
                .addOnFailureListener { Log.d("abcd", "메세지 삭제 실패.") }
        }
    }

    /** 메세지 추가 */
    suspend fun insertMessage(
        ImageUri: String? = null,
        sender: String,
        otherUid: String,
        newMessage: String? = null,
        timestamp: Long,
        otherNickname: String,
        chatId: String
    ): Flow<Message> {

        return flow<Message> {
            //삭제할 때 필요함
            val messageId = messageRef.push().key
            val message: Message

            if (newMessage == "photo" && ImageUri !== null) {
                //사진 보내기
                message = Message(sender, otherUid, ImageUri, "photo", messageId, timestamp, false)
            } else {
                //메세지만 보내기
                message = Message(sender, otherUid, null, newMessage, messageId, timestamp, false)
            }

            messageRef.child(chatId).child(messageId!!).setValue(message)
                .addOnCompleteListener {
                    Log.d("Abcd", "메세지 보내기 성공 : ${messageId} ")
                    //last message 업데이트 todo : 이게 최선인가?..
                    updateChatRoom(sender, newMessage!!, otherUid, timestamp, otherNickname)
                }
                .addOnFailureListener { Log.d("Abcd", "메세지 보내기 실패 : ${messageId} ") }

            emit(message)
        }.flowOn(Dispatchers.IO)
    }

    /** 채팅룸 업데이트 : lastmessage 및 timeStamp */
    fun updateChatRoom(
        sender: String,
        lastMessage: String,
        otherUid: String,
        timestamp: Long,
        otherNickname: String
    ) {

//        var profileImg:StorageReference? =  storage.reference.child("User/$otherUid/profileImages/${otherUid.plus(".jpeg")}")
//        Log.d("abcd","profileimg path is : ${profileImg}")
//
//        val chatroom = ChatRoom(profileImg?.path,otherUid,otherNickname,lastMessage,timestamp,false)
//        val chatRoomValue = chatroom.toMap()
//        val chatRoomUpdate = hashMapOf<String,Any>(
//            "ChatRoom/$sender/$otherUid" to chatRoomValue
//        )
//
//        database.reference.updateChildren(chatRoomUpdate)
//            .addOnCompleteListener {
//                Log.d("abcd","채팅룸 업데이트 성공.")
//            }
//            .addOnFailureListener { Log.d("abcd","채팅룸 업데이트 실패.") }


    }

    /** 내 채팅방내 메세지 가져오기 */
    suspend fun getMessageData(chatId:String): LiveData<MutableList<Message>> {

        val listData: MutableList<Message> = mutableListOf<Message>()
        val mutableData = MutableLiveData<MutableList<Message>>()


       CoroutineScope(Dispatchers.IO).launch {
           //대화방 메시지 -> 각 대화방의 멤버를 찾아서 넣어줌
           messageRef.orderByKey().equalTo(chatId).addChildEventListener(object : ChildEventListener {
               override fun onChildAdded(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                       Log.d("abcd", "messages.ref is ${messages.ref}")
                       for (message in messages.children) {
                           val getData = message.getValue(Message::class.java)
                           //멤버의 상대방과 메세지의 상대방이 일치할 시,
                           listData.add(getData!!)
                       }
                       mutableData.value = listData

                   } else {
                       Log.d("abcd","load할 메세지가 없습니다.")
                   }
               }
               //메세지가 새로 추가되거나, 메세지 삭제버튼을 눌러서 "message text 변경"
               override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                       Log.d("abcd","messages is : ${messages.ref}")
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
       }


        return mutableData
    }


    /** 내 채팅방 가져오기 */
    suspend fun getChatRoomData(): LiveData<MutableList<ChatRoom>> {

        val mutableData = MutableLiveData<MutableList<ChatRoom>>()
        val listData: MutableList<ChatRoom> = mutableListOf<ChatRoom>()

        //chatid
        userChatRef.orderByKey().equalTo(auth.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(chatIds: DataSnapshot) {
                    if (chatIds.exists()) {
                        for (chatId in chatIds.children) {
                            val getChatId = chatId.getValue(UserChat::class.java)
                            Log.d("abcd", "getChatid is ; ${getChatId?.chatId}")

                            //채팅룸
                            talkRef.orderByChild(getChatId?.chatId!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(chatRooms: DataSnapshot) {
                                        if (chatRooms.exists()) {

                                            for (chatRoom in chatRooms.children) {
                                                Log.d("abcd", "chatroom ref is : ${chatRoom.ref}")
                                                val getChatRoom =
                                                    chatRoom.getValue(ChatRoom::class.java)
                                                listData.add(getChatRoom!!)
                                            }
                                            mutableData.value = listData
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("abcd", "error in : ${error.message}")
                                    }

                                })
                        }

                    } else {
                        Log.d("abcd", "없음")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd", "error in getChatId")
                }
            })

        return mutableData
    }

//        talkRef.orderByKey().equalTo(myUid).addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(chatRooms: DataSnapshot, previousChildName: String?) {
//                if (chatRooms.exists()) {
//                    listData.clear()
//                    //chatId 가져오기
//
//
//                    Log.d("abcd","chat room is : ${chatRooms.ref}")
//                    for(chatRoom in chatRooms.children) {
//                        val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
//                        listData.add(getChatRoom!!)
//                    }
//                    mutableData.value = listData
//                } else {
//                    Log.d("abcd","chatRooms이 없음 ")
//                }
//            }
//
//            override fun onChildChanged(chatRooms: DataSnapshot, previousChildName: String?) {
//                if (chatRooms.exists()) {
//                    if (chatRooms.exists()) {
//                        listData.clear()
//                        Log.d("abcd","chat room is : ${chatRooms.ref}")
//                        for(chatRoom in chatRooms.children) {
//                            val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
//                            listData.add(getChatRoom!!)
//                        }
//                        mutableData.value = listData
//                    } else {
//                        Log.d("abcd","chatRooms이 없음 ")
//                    }
//
//                } else {
//                    Log.d("abcd","chatRooms이 없음 ")
//                }
//            }
//
//            override fun onChildRemoved(chatRooms: DataSnapshot) {
//                Log.d("abcd","chatRooms : "+chatRooms.value)
//            }
//
//            override fun onChildMoved(chatRooms: DataSnapshot, previousChildName: String?) {
//                Log.d("abcd","chatRooms : "+chatRooms.value)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("abcd","error in ChatRepo at getChatData : "+error.message)
//            }
//
//        })


    /** 채팅 시작 */
    suspend fun checkedChat(user: User): LiveData<String> {

            val otherUid = user.uid
            val myUid = auth.uid
            val result:MutableLiveData<String> = MutableLiveData("")
            Log.d("abcd", "상대방 uid : " + otherUid)
            Log.d("abcd", "내 uid : " + myUid)


             memberRef.child(myUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(members: DataSnapshot) {
                    Log.d("abcd", "snapshot is : " + members.value)

                    if (members.exists()) {
                        for (member in members.children) {
                            Log.d("abcd", "member.value is : " + member.value)
                            val getMember =
                                member.getValue(com.example.dowoom.model.Member::class.java)
                            //똑같은 멤버로 구성된 채팅방이 있는지 보기
                            if (getMember?.otherUid == otherUid && getMember?.myUid == myUid) {
                                //todo : 있는 곳으로 가야 됨
                                Log.d("abcd", "other 같음 : " + getMember.otherUid + " : " + otherUid)
                                Log.d("abcd", "myuid 같음 : " + getMember.myUid + " : " + myUid)
                            } else { //새로운 채팅방
                                result.value = initialChat(myUid, user)
                            }
                        }
                    } else {
                        result.value = initialChat(myUid, user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd", "fuck away from me in ChatRepo : " + error.message)
                }


            })
        return result
    }

    //user : 상대방임
     fun initialChat(myUid:String,user:User) : String {

        val messageId = messageRef.push().key
        val chatId = userChatRef.push().key

        val time = System.currentTimeMillis()/1000

        val myUserChat = UserChat(chatId!!, myUid, user.uid)
        val otherUserChat = UserChat(chatId, user.uid,myUid)
        val message = Message( myUid, user.uid, null, "안녕하세요.", messageId,time, false)
        val chatRoom = ChatRoom(user.profileImg,user.uid ,user.nickname, "안녕하세요.", time, false, chatId)

        //상대방
        userChatRef.child(user.uid!!).setValue(otherUserChat)
            .addOnCompleteListener {  Log.d("abcd", "상대방 userChat 성공") }
            .addOnFailureListener {Log.d("abcd", "상대방 userChat 실패")}
        //나
        userChatRef.child(myUid).setValue(myUserChat)
            .addOnCompleteListener {Log.d("abcd", "내 userChat 성공")}
            .addOnFailureListener { Log.d("abcd", "내 userChat 실패") }
        //채팅룸
        talkRef.child(chatId).setValue(chatRoom)
            .addOnCompleteListener { Log.d("abcd", "chat 성공") }
            .addOnFailureListener { Log.d("abcd", "chat 실패") }

        //메세지
        messageRef.child(chatId).child(messageId!!).setValue(message)
            .addOnCompleteListener { Log.d("abcd", "message 성공") }
            .addOnFailureListener { Log.d("abcd", "message 실패") }

        return chatId

    }



}
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

    //chatId
    val userChatRef = rootRef.child("UserChat")

    /** 메세지 삭제 */
    suspend fun deleteMessage(
        messageId: String,
        otherUid: String,
        timeStamp: Long,
        sender: String,
        chatId:String,
    ) {
        //chatid 구하기
        CoroutineScope(Dispatchers.IO).launch {

            val message = Message(sender, otherUid, null, "삭제되었습니다.", messageId, timeStamp, true)
            val messageValue = message.toMap()
            val childUpdates = hashMapOf<String, Any>(
                "/Message/${chatId}/$messageId" to messageValue
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
        chatId: String) : LiveData<Message> {

        val _message = MutableLiveData<Message>()
        val mutable : LiveData<Message> = _message

        CoroutineScope(Dispatchers.IO).launch {
            val messageId = messageRef.push().key
            lateinit var message: Message

            if (newMessage == "photo" && ImageUri !== null) {
                //사진 보내기
                message = Message(sender, otherUid, ImageUri, "photo", messageId, timestamp, false)
            } else {
                //메세지만 보내기
                message = Message(sender, otherUid, null, newMessage, messageId, timestamp, false)
            }


                messageRef.child(chatId).push().setValue(message)
                    .addOnCompleteListener {
                        Log.d("Abcd", "메세지 보내기 성공 : ${messageId} ")
                        //last message 업데이트 todo : 이게 최선인가?..
                        updateChatRoom(newMessage!!, timestamp, chatId)
                    }
                    .addOnFailureListener { Log.d("Abcd", "메세지 보내기 실패 : ${messageId} ") }

        }

        return  mutable

    }
    //todo : 홈 유저 대화방 갔다가 나오면 안뜨는거, 채팅방 update
    //todo : 홈 유저 대화방 갔다가 나오면 안뜨는거, 채팅방 update
    //todo : 홈 유저 대화방 갔다가 나오면 안뜨는거, 채팅방 update
    //todo : 홈 유저 대화방 갔다가 나오면 안뜨는거, 채팅방 update



    /** 채팅룸 업데이트 : lastmessage 및 timeStamp */
    fun updateChatRoom(
        lastMessage: String,
        timestamp: Long,
        chatId:String
    ) {

        talkRef.ref.child("$chatId/lastMessage").setValue(lastMessage).addOnCompleteListener {
            Log.d("abcd","!@!@")
        }
        talkRef.ref.child("$chatId/timeStamp").setValue(timestamp).addOnCompleteListener {
            Log.d("abcd","!@!@")
        }

    }

    /** 내 채팅방내 메세지 가져오기 */
    suspend fun getMessageData(chatId:String): LiveData<MutableList<Message>> {

        val listData: MutableList<Message> = mutableListOf<Message>()
        val mutableData = MutableLiveData<MutableList<Message>>()

        Log.d("abcd","chatid is in getmessagedata is : ${chatId} ")

       CoroutineScope(Dispatchers.IO).launch {
           //대화방 메시지 -> 각 대화방의 멤버를 찾아서 넣어줌
           messageRef.child(chatId).addChildEventListener(object : ChildEventListener {
               override fun onChildAdded(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                        Log.d("abcd","messages is : ${messages.ref}")

                       val getMessage = messages.getValue(Message::class.java)
                       listData.add(getMessage!!)
                       mutableData.value = listData
                   } else {
                       Log.d("abcd","load할 메세지가 없습니다.")
                   }
               }

               //메세지가 새로 추가되거나, 메세지 삭제버튼을 눌러서 "message text 변경"
               override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                      val getMessage = messages.getValue(Message::class.java)
                       if (getMessage?.message != "삭제되었습니다.") {
                           listData.add(getMessage!!)
                       } else {
                           //"삭제되었습니다."
                       }

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


    /** 채팅방 가져오기 */
    suspend fun getChatRoomData(): LiveData<MutableList<ChatRoom>> {

        val mutableData = MutableLiveData<MutableList<ChatRoom>>()
        val listData: MutableList<ChatRoom> = mutableListOf<ChatRoom>()


        //chatid
        Log.d("abcd","authuid is : ${auth.uid}")
        userChatRef.child(auth.uid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(chatId: DataSnapshot, previousChildName: String?) {
                if (chatId.exists()) {

                    Log.d("abcd","snapshot.ref is : ${chatId.key}")

                    //채팅룸
                    talkRef.child(chatId.key.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(chatRoom: DataSnapshot) {

                                val getChatRoom = chatRoom.getValue(ChatRoom::class.java)

                                listData.add(getChatRoom!!)
                                mutableData.value =listData
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("abcd"," error is : ${error.message}")
                            }
                    })


                } else {
                    Log.d("abcd","chatid가 없음")
                }
            }

            override fun onChildChanged(chatId: DataSnapshot, previousChildName: String?) {
                if (chatId.exists()) {
                    //채팅룸
                    listData.clear()

                    talkRef.child(chatId.key.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(chatRoom: DataSnapshot) {

                                val getChatRoom = chatRoom.getValue(ChatRoom::class.java)

                                listData.add(getChatRoom!!)
                                mutableData.value =listData
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("abcd"," error is : ${error.message}")
                            }
                        })
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("abcd","snapshot value in chatrepo is  : ${snapshot.value}")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("abcd","snapshot value in chatrepo is  : ${snapshot.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("abcd","error in chatrepo is  : ${error.message}")
            }


        })
        return mutableData

    }

    /** 채팅 시작 */
    suspend fun checkedChat(user: User): LiveData<String> {

            val otherUid = user.uid
            val myUid = auth.uid

            val _result = MutableLiveData<String>()
            val result : LiveData<String> = _result

            CoroutineScope(Dispatchers.IO).launch {
                userChatRef.child(myUid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userChats: DataSnapshot) {

                        if (userChats.exists()) {
                            for (userChat in userChats.children) {
                                Log.d("abcd", "userChat.value is : " + userChat.value)
                                val getUserChat = userChat.getValue(UserChat::class.java)

                                Log.d("abcd","getuserChat value is : ${getUserChat}")
                                //똑같은 멤버로 구성된 채팅방이 있는지 보기
                                if (getUserChat?.otherUid == otherUid && getUserChat?.myUid == myUid) {
                                    Log.d("abcd","여기??? 1")
                                    _result.value = getUserChat.chatId!!

                                } else { //새로운 채팅방
                                    Log.d("abcd","여기??? 2")
                                    _result.value = initialChat(myUid, user)
                                }
                            }
                        } else {
                            Log.d("abcd","여기??? 3")
                            _result.value = initialChat(myUid, user)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("abcd", "fuck away from me in ChatRepo : " + error.message)
                    }


                })
            }


        return result
    }

    //user : 상대방임
     fun initialChat(myUid:String,user:User) : String {

        val messageId = messageRef.push().key
        val chatId = userChatRef.push().key

        val time = System.currentTimeMillis()/1000

        val myUserChat = UserChat(chatId!!, myUid, user.uid)
        val otherUserChat = UserChat(chatId, user.uid,myUid)
        val message = Message(myUid, user.uid, null, "안녕하세요.", messageId,time, false)
        val chatRoom = ChatRoom(user.profileImg,user.uid ,user.nickname, "안녕하세요.", time, false, chatId)

        //상대방
        userChatRef.child(user.uid!!).child(chatId).setValue(otherUserChat)
            .addOnCompleteListener {  Log.d("abcd", "상대방 userChat 성공") }
            .addOnFailureListener {Log.d("abcd", "상대방 userChat 실패")}
        //나
        userChatRef.child(myUid).child(chatId).setValue(myUserChat)
            .addOnCompleteListener {Log.d("abcd", "내 userChat 성공")}
            .addOnFailureListener { Log.d("abcd", "내 userChat 실패") }
        //채팅룸
        talkRef.child(chatId).setValue(chatRoom)
            .addOnCompleteListener { Log.d("abcd", "chat 성공") }
            .addOnFailureListener { Log.d("abcd", "chat 실패") }

//        //메세지
        messageRef.child(chatId).child(messageId!!).setValue(message)
            .addOnCompleteListener { Log.d("abcd", "message 성공") }
            .addOnFailureListener { Log.d("abcd", "message 실패") }

        return chatId

    }



}
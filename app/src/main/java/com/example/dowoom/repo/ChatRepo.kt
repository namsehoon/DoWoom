package com.example.dowoom.repo

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.dowoom.Util.HandleImage
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Message
import com.example.dowoom.model.User
import com.example.dowoom.model.UserChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
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

    /** chat id 가져오기 */
    suspend fun getChatIdRepo(otherUid: String) : LiveData<String> {

        val _chatId = MutableLiveData<String>()
        val chatId : LiveData<String>  = _chatId


        val myuid = FirebaseAuth.getInstance().currentUser?.uid

        CoroutineScope(Dispatchers.IO).launch {
            userChatRef.child(myuid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("abcd","getChatIdRepo ref is ; ${snapshot.ref}")
                        for (child in snapshot.children) {

                            val getChatid = child.getValue(UserChat::class.java)

                            //만약 "내"가 제공한 내 uid를 사용하여 "상대방"의 uid가 같다면, chatid 리턴
                            if (getChatid?.otherUid.equals(otherUid)) {
                                _chatId.value = getChatid?.chatId.toString()
                                Log.d("abcd","get chatid in viewmodel getChatid() is : ${getChatid?.chatId}")
                            } else {
                                Log.d("Abcd","다릅니다")
                            }
                            Log.d("abcd","getchatid is : ${getChatid}")
                        }
                    } else {
                        Log.d("abcd","snapshot doesnt exist in ChatViewModel ")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","error in chatviewmodel is :${error.message}")
                }

            })
        }


        return chatId
    }


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

        Log.d("abcd","chatId is : ${chatId}")

        CoroutineScope(Dispatchers.IO).launch {

            //todo : 내 userchat에 있는 상대방의 uid의 채팅방을 검색해서 내가 없으면
            userChatRef.child(otherUid).child(chatId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(result: DataSnapshot) {
                    //처음 대화
                    if (!result.exists()) {
                        val otherUserChat = UserChat(chatId, otherUid, auth.uid)

                        userChatRef.child(otherUid).child(chatId).setValue(otherUserChat)
                            .addOnCompleteListener {  Log.d("abcd", "상대방 userChat 성공") }
                            .addOnFailureListener {Log.d("abcd", "상대방 userChat 실패")}

                    }

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
                            updateChatRoom(newMessage!!,timestamp,chatId)
                        }
                        .addOnFailureListener { Log.d("Abcd", "메세지 보내기 실패 : ${messageId} ") }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","error in insert message : ${error.message}")
                }

            })


        }

        return  mutable

    }


    /** 채팅룸 업데이트 : lastmessage 및 timeStamp */
    fun updateChatRoom(
        lastMessage: String,
        timestamp: Long,
        chatId:String
    ) {

        /////
        //contructor
        val chatRoom = ChatRoom(lastMessage,timestamp)
        //mapping
        val chatRoomValue: Map<String, Any?> = chatRoom.toMap()
        //db 참조
        val updateTalkRef = talkRef.child(chatId)

        val childUpdates = hashMapOf<String,Any>(
             "lastMessage" to lastMessage,
            "timeStamp" to timestamp
        )

        updateTalkRef.updateChildren(childUpdates).addOnCompleteListener {
            Log.d("abcd", "chatroom update 성공")
        }

    }

    /** 내 채팅방내 메세지 가져오기 */
    suspend fun getMessageData(chatId:String): LiveData<MutableList<Message>> {

        val listData: MutableList<Message> = mutableListOf<Message>()
        val mutableData = MutableLiveData<MutableList<Message>>()

        Log.d("abcd","chatid is in getmessagedata is  33 : ${chatId} ")

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

//onChildAdded(): 리스트의 아이템을 검색하거나 아이템의 추가가 있을때 수신합니다.
//onChildChanged(): 아이템의 변화가 있을때 수신합니다.
//onChildRemoved(): 아이템이 삭제되었을때 수신합니다.
//onChildMoved(): 순서가 있는 리스트에서 순서가 변경되었을때 수신합니다.

//todo 채팅방 마지막메세지 업데이트 및 채팅방 삭제, 메세지 삭제

        CoroutineScope(Dispatchers.IO).launch {
            userChatRef.child(auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("abcd","getChatIdRepo ref is ; ${snapshot.ref}")
                        for (child in snapshot.children) {

                            val getChatid = child.getValue(UserChat::class.java)
                            Log.d("abcd","getchatid is : ${getChatid}")

                            talkRef.orderByChild(getChatid?.chatId!!)
                                .addChildEventListener(object : ChildEventListener {
                                    override fun onChildAdded(
                                        chatRooms: DataSnapshot,
                                        previousChildName: String?
                                    ) {
                                        if (chatRooms.exists()) {
//                                            Log.d("abcd","ref is ${chatRooms.key}")
                                        val getva = chatRooms.getValue(ChatRoom::class.java)
                                            listData.add(getva!!)
                                        Log.d("ABCD","getva add is ${getva}")
                                            mutableData.value = listData
                                        }
                                    }

                                    override fun onChildChanged(
                                        chatRooms: DataSnapshot,
                                        previousChildName: String?
                                    ) {
                                        if (chatRooms.exists()) {
//                                            Log.d("abcd","ref is ${chatRooms.key}")
                                            val getva = chatRooms.getValue(ChatRoom::class.java)
                                            Log.d("ABCD","getva changed is ${getva}")
                                        }
                                    }

                                    override fun onChildRemoved(snapshot: DataSnapshot) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onChildMoved(
                                        snapshot: DataSnapshot,
                                        previousChildName: String?
                                    ) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        }
                    } else {
                        Log.d("abcd","snapshot doesnt exist in ChatViewModel ")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","error in chatviewmodel is :${error.message}")
                }

            })
        }



        //chatid
//        Log.d("abcd","authuid is : ${auth.uid}")
//        userChatRef.child(auth.uid).addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(chatId: DataSnapshot, previousChildName: String?) {
//                if (chatId.exists()) {
//
//                    Log.d("abcd","snapshot.ref is : ${chatId.key}")
//
//                    //마지막메세지
//                    messageRef.child(chatId.key.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(messages: DataSnapshot) {
//                            val getLastMessage = messages.children.last().getValue(Message::class.java)
//                            Log.d("abcd","getLastMessage is : ${getLastMessage}")
//
//                            //채팅룸
//                            talkRef.child(chatId.key.toString())
//                                .addListenerForSingleValueEvent(object : ValueEventListener {
//                                    override fun onDataChange(chatRoom: DataSnapshot) {
//
//                                        val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
//
//                                        //채팅룸의 lastMessage와 메세지의 lastMessage가 다르다면 업데이트
//                                        if (!getChatRoom?.lastMessage.equals(getLastMessage?.message)) {
//                                            talkRef.child(chatId.key.toString()).child("lastMessage").setValue(getLastMessage?.message)
//                                            talkRef.child(chatId.key.toString()).child("timeStamp").setValue(getLastMessage?.timeStamp)
//                                        }
//
//                                        getChatRoom?.lastMessage = getLastMessage?.message
//                                        listData.add(getChatRoom!!)
//                                        mutableData.value =listData
//                                    }
//
//                                    override fun onCancelled(error: DatabaseError) {
//                                        Log.d("abcd"," error getChatRoomData is : ${error.message}")
//                                    }
//                                })
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.d("Abcd","error getChatRoomData is : ${error.message}")
//                        }
//                    })
//
//                } else {
//                    Log.d("abcd","chatid가 없음")
//                }
//            }
//
//            override fun onChildChanged(chatId: DataSnapshot, previousChildName: String?) {
//                if (chatId.exists()) {
//                    //채팅룸
//
//                    //마지막메세지
//                    messageRef.child(chatId.key.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(messages: DataSnapshot) {
//                            val getLastMessage = messages.children.last().getValue(Message::class.java)
//                            Log.d("abcd","getLastMessage is : ${getLastMessage}")
//
//                            //채팅룸
//                            talkRef.child(chatId.key.toString())
//                                .addListenerForSingleValueEvent(object : ValueEventListener {
//                                    override fun onDataChange(chatRoom: DataSnapshot) {
//
//                                        val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
//
//                                        //채팅룸의 lastMessage와 메세지의 lastMessage가 다르다면 업데이트
//                                        if (!getChatRoom?.lastMessage.equals(getLastMessage?.message)) {
//                                            talkRef.child(chatId.key.toString()).child("lastMessage").setValue(getLastMessage?.message)
//                                            talkRef.child(chatId.key.toString()).child("timeStamp").setValue(getLastMessage?.timeStamp)
//                                        }
//
//                                        getChatRoom?.lastMessage = getLastMessage?.message
//                                        listData.add(getChatRoom!!)
//                                        mutableData.value =listData
//                                    }
//
//                                    override fun onCancelled(error: DatabaseError) {
//                                        Log.d("abcd"," error getChatRoomData is : ${error.message}")
//                                    }
//                                })
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.d("Abcd","error getChatRoomData is : ${error.message}")
//                        }
//                    })
//                }
//            }
//            //대화방 삭제
//            override fun onChildRemoved(chatId: DataSnapshot) {
//                if(chatId.exists()) {
//
//                    Log.d("abcd","snapshot.ref is : ${chatId.key}")
//
//                    //채팅룸 삭제
//                    talkRef.child(chatId.key.toString())
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(chatRoom: DataSnapshot) {
//
//                                val getChatRoom = chatRoom.getValue(ChatRoom::class.java)
//
//                                listData.remove(getChatRoom!!)
//                                mutableData.value =listData
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                Log.d("abcd"," error getChatRoomData is : ${error.message}")
//                            }
//                        })
//                }
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("abcd","snapshot value in chatrepo is  : ${snapshot.value}")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("abcd","userChat() error in chatrepo is  : ${error.message}")
//            }
//
//
//        })
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


        val chatId = userChatRef.push().key

        val time = System.currentTimeMillis()/1000

        val member = com.example.dowoom.model.Member(myUid, user.uid)
        val chatRoom = ChatRoom(user.profileImg,user.uid ,user.nickname, "안녕하세요.", time, false, chatId, member)
        val myUserChat = UserChat(chatId!!, myUid, user.uid)


        //나
        userChatRef.child(myUid).child(chatId).setValue(myUserChat)
            .addOnCompleteListener {Log.d("abcd", "내 userChat 성공")}
            .addOnFailureListener { Log.d("abcd", "내 userChat 실패") }
        //채팅룸
        talkRef.child(chatId).setValue(chatRoom)
            .addOnCompleteListener { Log.d("abcd", "chat 성공") }
            .addOnFailureListener { Log.d("abcd", "chat 실패") }

        messageRef.setValue(chatId)
            .addOnCompleteListener { Log.d("abcd", "message 성공") }
            .addOnFailureListener { Log.d("abcd", "message 실패") }

        return chatId

    }



}
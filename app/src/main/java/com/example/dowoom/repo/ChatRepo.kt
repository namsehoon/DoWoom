package com.example.dowoom.repo

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.dowoom.Util.HandleImage
import com.example.dowoom.adapter.chatMsgAdatper
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Message
import com.example.dowoom.model.User
import com.example.dowoom.model.UserChat
import com.example.dowoom.viewmodel.registervm.LoadingViewmodel
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

    val myuid = FirebaseAuth.getInstance().currentUser?.uid

    /** chat id 가져오기 */
    suspend fun getChatIdRepo(otherUid: String) : LiveData<String> {

        val _chatId = MutableLiveData<String>()
        val chatId : LiveData<String>  = _chatId

        CoroutineScope(Dispatchers.Default).launch {
            userChatRef.child(myuid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("abcd","getChatIdRepo ref is ; ${snapshot.ref}")
                        for (child in snapshot.children) {

                            val getChatid = child.getValue(UserChat::class.java)

                            //만약 "내"가 제공한 내 uid를 사용하여 "상대방"의 uid가 같다면, chatid 리턴
                            if (getChatid?.otherUid.equals(otherUid)) {
                                _chatId.value = getChatid?.chatId.toString()
                                checkChatRoomMemberOneNull(getChatid?.chatId.toString())
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

    /** 만약 채팅 멤버 한명만 null이면, 메세지 작성 및 버튼 비활성화 */

    val _memberResult = MutableLiveData<Boolean>()
    val memberResult : LiveData<Boolean>  = _memberResult

    fun checkChatRoomMemberOneNull(chatId: String) : LiveData<Boolean> {


        CoroutineScope(Dispatchers.IO).launch {
            talkRef.child(chatId).child("member").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("abcd", "checkChatRoomMemberOneNull ref is : ${snapshot.ref}")
                    val mem = snapshot.getValue(com.example.dowoom.model.Member::class.java)
                    _memberResult.value = mem?.user2 == null || mem.user1 == null
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","checkChatRoomMemberOneNull error is : ${error.message}")
                }

            })
        }

        return memberResult
    }


    /** 메세지 삭제 */
    suspend fun deleteMessage(
        messageId: String,
        chatId:String,
    ) {
        //chatid 구하기
        CoroutineScope(Dispatchers.IO).launch {

            val childUpdates = hashMapOf<String, Any>(
                "message" to "삭제되었습니다."
            )
            messageRef.child(chatId).child(messageId).updateChildren(childUpdates)
                .addOnCompleteListener { Log.d("abcd", "메세지 삭제 성공.") }
                .addOnFailureListener { Log.d("abcd", "메세지 삭제 실패.") }
        }
    }

    //todo : 터미널 에러 : Ignoring header X-Firebase-Locale because its value was null.
    /** 채팅방 삭제 */
    suspend fun deleteChatRoom(
        chatId: String,
        member: com.example.dowoom.model.Member,
    ) {
        //각 사용자가 대화방 나갈 때
        CoroutineScope(Dispatchers.IO).launch {
            if (member.user1 == auth.uid) { // user1이 나라면,
                //chatRoom 의 member 중의 '나'를 false로 만들기
                talkRef.child(chatId).child("member").child("user1").setValue(null)
                    .addOnCompleteListener{
                        checkChatRoomMemberBothNull(chatId, member)
                        Log.d("abcd","(deleteChatRoom) chatRoom 의 member중의 나 : false")
                    }
                // userChat chatid 삭제
                userChatRef.child(member.user1!!).child(chatId).removeValue()
                    .addOnCompleteListener {
                        Log.d("abcd","(deleteChatRoom) userChat chaid 삭제")
                    }
                //1. member : false, 메세지 et : disabled -> messageinsert()
            } else {
                talkRef.child(chatId).child("member").child("user2").setValue(null)
                    .addOnCompleteListener{
                        Log.d("abcd","chatRoom 의 member중의 나 : false")
                        checkChatRoomMemberBothNull(chatId, member)
                    }
                userChatRef.child(member.user2!!).child(chatId).removeValue()
                    .addOnCompleteListener {
                        Log.d("abcd","(deleteChatRoom) userChat chaid 삭제")
                    }
            }
        }
    }

    /** 만약 채팅 멤버가 둘다 null 이면, chatroom, message 삭제 */
     fun checkChatRoomMemberBothNull(chatId: String, member: com.example.dowoom.model.Member) {

        CoroutineScope(Dispatchers.IO).launch {
            if (member.user1.equals(null) && member.user2.equals(null)) {
                talkRef.child(chatId).removeValue().addOnCompleteListener {
                    Log.d("abcd","DB에서 채팅룸 삭제")
                }
                messageRef.child(chatId).removeValue().addOnCompleteListener {
                    Log.d("abcd","DB에서 메세지 삭제")
                }
            }
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
                       val addedMessageData = messages.getValue(Message::class.java)
                       Log.d("abcd","addedMessageData is : ${addedMessageData}")
                       listData.add(addedMessageData!!)
                       mutableData.value = listData
                   } else {
                       Log.d("abcd","load할 메세지가 없습니다.")
                   }
               }

               //메세지가 새로 추가되거나, 메세지 삭제버튼을 눌러서 "message text 변경"
               override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                      val changedMessageData = messages.getValue(Message::class.java)
                       Log.d("abcd","changedMessageData is ${changedMessageData}")
                       if (changedMessageData?.message != "삭제되었습니다.") {
                           listData.add(changedMessageData!!)
                       } else {
                           //"삭제되었습니다."
                       }

                   }
               }

               override fun onChildRemoved(snapshot: DataSnapshot) {
                   Log.d("abcd","getMessageData onChildRemoved is : "+snapshot.ref)
               }

               override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                   Log.d("abcd","getMessageData onChildMoved is : "+snapshot.ref)
               }

               override fun onCancelled(error: DatabaseError) {
                   Log.d("abcd","error in getMessageData  is : "+error.message)
               }

           })

       }


        return mutableData
    }
    /** chatid들 가져오기 */
    suspend fun observeChatId(): LiveData<MutableList<String>> {
        val mutableData = MutableLiveData<MutableList<String>>()
        val listData: MutableList<String> = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {

            userChatRef.child(auth.uid).addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(chatIds: DataSnapshot, previousChildName: String?) {
                    if (chatIds.exists()) {

                        listData.add(chatIds.key!!)
                        mutableData.value = listData
                    }
                }

                override fun onChildChanged(chatIds: DataSnapshot, previousChildName: String?) {
                    //todo UserChat 안해도 됨
                    if (chatIds.exists()) {

                        Log.d("abcd","observeChatId() onChildChanged is : ${chatIds.key}")

                    }
                }

                override fun onChildRemoved(chatIds: DataSnapshot) {
                    if (chatIds.exists()) {
                        listData.remove(chatIds.key)
                        mutableData.value = listData
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd", "error in observeChatId is : ${error.message}")
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

//todo 메세지 삭제, 사진 저장


            //유저 각자 가지고 있는 chat id들을 가져옴
            observeChatId().observeForever(androidx.lifecycle.Observer {  chatIdList ->

                Log.d("abcd", "chatIdList in getChatRoomData is : ${chatIdList.toString()}")

                chatIdList.forEach { chatId ->

                    Log.d("abcd", "chatId in getChatRoomData is : ${chatId.toString()}")

                        //각 chatid의 채팅룸을 채팅리스트에 뽑아줌 //todo 이게 최선?... ValueEventListener??..
                        talkRef.child(chatId).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(chatRoom: DataSnapshot) {
                                if (chatRoom.exists()) {
                                    listData.clear()

                                    val chatRoomValue = chatRoom.getValue(ChatRoom::class.java)
                                    listData.add(chatRoomValue!!)
                                    mutableData.value = listData
                                } else {
                                    Log.d("abcd","없음")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("abcd","채팅리스트 에러 : ${error.message}")
                            }

                        })


                }
            })



        //  5/9 2:52
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
package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.talkModel.ChatRoom
import com.example.dowoom.model.talkModel.Message
import com.example.dowoom.model.User
import com.example.dowoom.model.talkModel.UserChat
import com.example.dowoom.model.talkModel.Member
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.*

class ChatRepo {



    /** 만약 채팅 멤버 한명만 null이면, 메세지 작성 및 버튼 비활성화 */

    val _memberResult = MutableLiveData<Boolean>()
    val memberResult : LiveData<Boolean>  = _memberResult
//todo : 수정
    fun checkChatRoomMemberOneNull(chatId: String) : LiveData<Boolean> {


        CoroutineScope(Dispatchers.IO).launch {
            Ref().chatroomRef().child(chatId).child("member").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("abcd", "checkChatRoomMemberOneNull ref is : ${snapshot.ref}")
                    val mem = snapshot.getValue(Member::class.java)
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

            val updateMsg = Ref().messageRef().child(chatId).child(messageId)

            val childUpdates = hashMapOf<String, Any>(
                "message" to "삭제되었습니다."
            )
            Log.d("abcd","message id : ${messageId}, chatId is : ${chatId}")
            updateMsg.updateChildren(childUpdates)
                .addOnCompleteListener { Log.d("abcd", "메세지 삭제 성공.") }
                .addOnFailureListener { Log.d("abcd", "메세지 삭제 실패.") }

        }
    }


    //todo : 터미널 에러 : Ignoring header X-Firebase-Locale because its value was null.
    /** 채팅방 삭제 */
//    suspend fun deleteChatRoom(
//        chatId: String,
//        member: Member,
//    ) {
//        //각 사용자가 대화방 나갈 때
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("abcd","여기까지 옴")
//            if (member.user1 == auth.uid) { // user1이 나라면,
//
//                // userChat chatid 삭제
//                userChatRef.child(member.user1!!).child(chatId).removeValue()
//                    .addOnCompleteListener {
//                        Log.d("abcd","(deleteChatRoom) userChat chaid 삭제")
//                        //상대방 uid로 검색해서 상대방도 null이면 db에서 삭제
//                        checkChatRoomMemberBothNull(chatId, member.user2!!)
//                    }
//                //1. member : false, 메세지 et : disabled -> messageinsert()
//            } else {
//
//                userChatRef.child(member.user2!!).child(chatId).removeValue()
//                    .addOnCompleteListener {
//                        Log.d("abcd","(deleteChatRoom) userChat chaid 삭제")
//                        checkChatRoomMemberBothNull(chatId, member.user1!!)
//                    }
//            }
//        }
//    }

    /** 만약 채팅 멤버가 둘다 null 이면, chatroom, message 삭제 */
//     fun checkChatRoomMemberBothNull(chatId: String, otherUid: String) {
//        //상대방 uid로 검색해서 상대방도 null이면 db에서 삭제
//        CoroutineScope(Dispatchers.IO).launch {
//            userChatRef.child(otherUid).child(chatId).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (!snapshot.exists()) {
//                        talkRef.child(chatId).removeValue().addOnCompleteListener {
//                            Log.d("abcd","DB에서 채팅룸 삭제")
//                        }
//                        messageRef.child(chatId).removeValue().addOnCompleteListener {
//                            Log.d("abcd","DB에서 메세지 삭제")
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//        }
//    }





    /** 메세지 보내기 */
    suspend fun sendMessage(imageUrl: String? = null, from: String, to: String, text: String? = null) {

        CoroutineScope(Dispatchers.IO).launch {

            val key = Ref().messageRef().push().key
            val time = System.currentTimeMillis()/1000
            var message:Message? = null
            if (imageUrl == null) {
                //텍스트
                message = Message(from,to,null,text,key,time,false)
            } else {
                //사진
                message = Message(from,to,imageUrl,"이미지",key,time,false)
            }


            Ref().messageRef().child(from).child(to).child(key!!).setValue(message)
                .addOnCompleteListener {
                    Log.d("abcd","chatRepo - sendMessage - 메세지 전송 성공")
                    updateChatRoom(from,to,message)
                }

        }


    }


    /** 채팅룸 업데이트 : lastmessage 및 timeStamp */
    fun updateChatRoom(from:String, to:String, message: Message) {

        val childUpdates = hashMapOf<String,Any>(
            "from" to from,
            "date" to message.date!!,
            "to" to to,
            "message" to message.message!!,
            "read" to message.read

        )
        //내가 상대방에게
        Ref().chatroomRef().child(from).child(to).updateChildren(childUpdates).addOnCompleteListener {
            Log.d("abcd", "chatRepo - updateChatRoom - 나")
        }

        //상대방이 나에게
        Ref().chatroomRef().child(to).child(from).updateChildren(childUpdates).addOnCompleteListener {
            Log.d("abcd", "chatRepo - updateChatRoom - 상대방")
        }

    }



    /** 메세지 가져오기 */
    suspend fun observeMessage(from: String,to: String): LiveData<Message> {

        val listData: MutableList<Message> = mutableListOf<Message>()
        val mutableData = MutableLiveData<Message>()

        //key 담기
        val idList:MutableList<String> = mutableListOf<String>()

           //대화방 메시지 -> 각 대화방의 멤버를 찾아서 넣어줌
        Ref().messageRef().child(from).child(to).addChildEventListener(object : ChildEventListener {
               override fun onChildAdded(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                       Log.d("Abcd","chatRepo - observeMessage - onChildAdded")

                       val messageData = messages.getValue(Message::class.java)
                       Log.d("abcd","messagedata is :${messages.ref}")

                       idList.add(messageData?.messageId!!)

                       mutableData.value = messageData!!
                   } else {
                       Log.d("abcd","load할 메세지가 없습니다.")
                   }
               }

               //메세지가 새로 추가되거나, 메세지 삭제버튼을 눌러서 "message text 변경"
               override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                       Log.d("Abcd","chatRepo - observeMessage - onChildChanged")
//                      val changedMessageData = messages.getValue(Message::class.java)
//                       Log.d("abcd","changedMessageData is ${changedMessageData}")
//                       Log.d("abcd","previousChildName is : ${previousChildName}")
//                       if (!changedMessageData?.message.equals("삭제되었습니다.")) {
//                           listData.add(changedMessageData!!)
//                           idList.add(changedMessageData.messageId!!)
//                       } else {
//                           //messageid의 인덱스
//                           val index = idList.indexOf(changedMessageData?.messageId)
//                           if (index > -1) {
//                               listData[index] = changedMessageData!!
//                           }
//                       }
//                       mutableData.value = listData

                   }
               }

               override fun onChildRemoved(snapshot: DataSnapshot) {
                   Log.d("Abcd","chatRepo - observeMessage - onChildRemoved")
               }

               override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                   Log.d("Abcd","chatRepo - observeMessage - onChildMoved")
               }

               override fun onCancelled(error: DatabaseError) {
                   Log.d("abcd","chatRepo - observeMessage - error : "+error.message)
               }

           })
        return mutableData
    }





    /** 채팅방 가져오기 */
//    suspend fun getChatRoomData(): LiveData<MutableList<ChatRoom>> {
//
//        val mutableData = MutableLiveData<MutableList<ChatRoom>>()
//        val listData: MutableList<ChatRoom> = mutableListOf<ChatRoom>()
//
////onChildAdded(): 리스트의 아이템을 검색하거나 아이템의 추가가 있을때 수신합니다.
////onChildChanged(): 아이템의 변화가 있을때 수신합니다.
////onChildRemoved(): 아이템이 삭제되었을때 수신합니다.
////onChildMoved(): 순서가 있는 리스트에서 순서가 변경되었을때 수신합니다.
//
////todo  사진 저장
//
//
//            //유저 각자 가지고 있는 chat id들을 가져옴
//            observeChatId().observeForever(androidx.lifecycle.Observer {  chatIdList ->
//
//                Log.d("abcd", "chatIdList in getChatRoomData is : ${chatIdList.toString()}")
//
//                chatIdList.forEach { chatId ->
//
//                    Log.d("abcd", "chatId in getChatRoomData is : ${chatId.toString()}")
//
//                        //각 chatid의 채팅룸을 채팅리스트에 뽑아줌 //todo 이게 최선?... ValueEventListener??..
//                        talkRef.child(chatId).addValueEventListener(object : ValueEventListener {
//                            override fun onDataChange(chatRoom: DataSnapshot) {
//                                if (chatRoom.exists()) {
//                                    listData.clear()
//
//                                    val chatRoomValue = chatRoom.getValue(ChatRoom::class.java)
//                                    listData.add(chatRoomValue!!)
//                                    mutableData.value = listData
//                                } else {
//                                    Log.d("abcd","없음")
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                Log.d("abcd","채팅리스트 에러 : ${error.message}")
//                            }
//
//                        })
//
//
//                }
//            })
//
//
//
//        //  5/9 2:52
//        return mutableData
//
//    }



}
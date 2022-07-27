package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.talkModel.ChatRoom
import com.example.dowoom.model.talkModel.Message
import com.example.dowoom.model.User
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.*


class ChatRepo {



    /** 채팅방 삭제 */ //Todo
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
//                }
//
//            })
//        }
//    }



    /** 메세지 보내기 */
    suspend fun sendMessage( from: String, to: String, text: String? = null) {

        CoroutineScope(Dispatchers.IO).launch {

            val key = Ref().messageRef().push().key
            val time = System.currentTimeMillis()/1000
            var message:Message? = null

            message = Message(from,to,null,text,key,time,false)


            Ref().messageRef().child(from).child(to).child(key!!).setValue(message)
                .addOnCompleteListener {
                    Log.d("abcd","chatRepo - sendMessage - 메세지 전송 성공")
                    updateChatRoom(from,to,message)
                }

        }


    }


    /** 채팅룸 업데이트 : lastmessage 및 timeStamp */
    fun updateChatRoom(from:String, to:String, message: Message) {

        val fromTochildUpdates = hashMapOf<String,Any>(
            "from" to from,
            "date" to message.date!!,
            "to" to to,
            "message" to message.message!!,
            "read" to message.read

        )
        val toFromchildUpdates = hashMapOf<String,Any>(
            "from" to to,
            "date" to message.date!!,
            "to" to from,
            "message" to message.message!!,
            "read" to message.read

        )

        //내가 상대방에게
        Ref().chatroomRef().child(from).child(to).updateChildren(fromTochildUpdates).addOnCompleteListener {
            Log.d("abcd", "chatRepo - updateChatRoom - 나")
        }

        //상대방이 나에게
        Ref().chatroomRef().child(to).child(from).updateChildren(toFromchildUpdates).addOnCompleteListener {
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
                       idList.add(messageData?.messageId!!)
                        listData.add(messageData)
                       mutableData.value = messageData!!
                   } else {
                       Log.d("abcd","load할 메세지가 없습니다.")
                   }
               }

               //메세지가 새로 추가되거나, 메세지 삭제버튼을 눌러서 "message text 변경"
               override fun onChildChanged(messages: DataSnapshot, previousChildName: String?) {
                   if (messages.exists()) {
                       Log.d("Abcd","chatRepo - observeMessage - onChildChanged")
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
    suspend fun getChatRoomData(uid:String): LiveData<MutableList<ChatRoom>> {

        val mutableData = MutableLiveData<MutableList<ChatRoom>>()
        val listData: MutableList<ChatRoom> = mutableListOf<ChatRoom>()
        val idList : MutableList<String> = mutableListOf<String>()

        Ref().chatroomRef().child(uid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Abcd","ChatRepo - getChatRoomData - onChildAdded")
                if (snapshot.exists()) {
                    Log.d("abcd","chatroom is : ${snapshot.ref}")

                    val chatroom = snapshot.getValue(ChatRoom::class.java)

                    Ref().userRef().child(snapshot.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                           val user = snapshot.getValue(User::class.java)
                            chatroom?.user = user
                            idList.add(user?.uid!!) //user id 저장
                            listData.add(0,chatroom!!)
                            mutableData.value = listData
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("Abcd","ChatRepo - getChatRoomData - onChildAdded - error : ${error.message}")
                        }

                    })
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Abcd","ChatRepo - getChatRoomData - onChildChanged")

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("Abcd","ChatRepo - getChatRoomData - onChildRemoved")
                if (snapshot.exists()) {

                    val chatroom = snapshot.getValue(ChatRoom::class.java)

                    Ref().userRef().child(snapshot.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            chatroom?.user = user
                            val index = idList.indexOf(user?.uid) //d
                            listData.removeAt(index)
                            mutableData.value = listData
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("Abcd","ChatRepo - getChatRoomData - onChildAdded - error : ${error.message}")
                        }

                    })
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Abcd","ChatRepo - getChatRoomData - onChildMoved")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Abcd","ChatRepo - getChatRoomData - error : ${error.message}")
            }

        })

        return mutableData

    }



}
package com.example.dowoom.repo

import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.model.comunityModel.Comment
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.model.comunityModel.ContentModel
import com.example.dowoom.model.gameModel.GameModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class ComuRepo : repo {


    val guestRef = rootRef.child("Comu/Guest")
    val contentRef = rootRef.child("Content")
    val commentRef = rootRef.child("Comment")

    /** 익명게시판 콘텐츠 리스트 */
    fun getGuestList() : LiveData<MutableList<ComuModel>> {

        val mutableData = MutableLiveData<MutableList<ComuModel>>()
        val guestList:MutableList<ComuModel> = mutableListOf<ComuModel>()

        CoroutineScope(Dispatchers.IO).launch {

            guestRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(guests: DataSnapshot, previousChildName: String?) {
                    if (guests.exists()) {
                        Log.d("abcd","ComuRepo - getGuestList - onChildAdded : ${guests.ref}")
                        val getGuest = guests.getValue(ComuModel::class.java)
                        guestList.add(getGuest!!)
                        mutableData.value = guestList
                    }
                }

                override fun onChildChanged(guests: DataSnapshot, previousChildName: String?) {
                    Log.d("abcd","ComuRepo - getGuestList - onChildAdded : ${guests.ref}")
                }

                override fun onChildRemoved(guests: DataSnapshot) {
                    Log.d("abcd","ComuRepo - getGuestList - onChildRemoved : ${guests.ref}")
                }

                override fun onChildMoved(guests: DataSnapshot, previousChildName: String?) {
                    Log.d("abcd","ComuRepo - getGuestList - onChildMoved : ${guests.ref}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","ComuRepo - getGuestList - error : ${error.message}")
                }

            })
        }

        return mutableData
    }


    /** 댓글 리스트 */
    fun getComments(comuModel: ComuModel) : LiveData<MutableList<Comment>> {

        val mutableData = MutableLiveData<MutableList<Comment>>()
        val commentList:MutableList<Comment> = mutableListOf<Comment>()

        commentRef.child(comuModel.uid!!).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(comments: DataSnapshot, previousChildName: String?) {
                Log.d("abcd","ComuRepo - getComments - onChildAdded - ${comments.ref}")
                if (comments.exists()) {
                    val commentData = comments.getValue(Comment::class.java)
                    commentList.add(commentData!!)
                    mutableData.value = commentList
                }
            }

            override fun onChildChanged(comments: DataSnapshot, previousChildName: String?) {
                Log.d("abcd","ComuRepo - getComments - onChildChanged - ${comments.ref}")
            }

            override fun onChildRemoved(comments: DataSnapshot) {
                Log.d("abcd","ComuRepo - getComments - onChildRemoved - ${comments.ref}")
            }

            override fun onChildMoved(comments: DataSnapshot, previousChildName: String?) {
                Log.d("abcd","ComuRepo - getComments - onChildMoved - ${comments.ref}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("abcd","ComuRepo - getComments - error - ${error.message}")
            }

        })

        return mutableData
    }

    /** 게시판 글 쓰기 */

    fun insertGuestWriteIn(subject: String, content: String) : Flow<Boolean> {
        return flow {
            val comuUid = guestRef.push().key
            val contentUid = contentRef.push().key

            // 게시판
            val comuModel = ComuModel(comuUid,subject,2,0,auth.uid,false, null,content,0,0)

            //내용 task
            val taskTwo = contentRef.child(comuUid!!).setValue(comuModel)
            //익게 task
            val task = guestRef.child(comuUid).setValue(comuModel).continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.d("abcd","ComuRepo - insertGuestWriteIn 테스크 성공")
                    task.exception?.let {
                        Log.d("abcd","ComuRepo - insertGuestWriteIn - 게시판 글 작성 실패")
                        throw  it
                    }
                }
                taskTwo
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("abcd","ComuRepo - insertGuestWriteIn - 게시판 글 작성 성공")
                }
            }

            //todo : task 관리 해야함.. delay?.. runblocking?
            delay(500)
            if (task.isSuccessful) {
                emit(true)
            } else {
                emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }

    /** 댓글 작성 */
    //todo : 하루에 한번씩 랜덤으로 바꿔줘야할 듯 (자바 스크립트 랜덤 문자, 숫자 조합)
    //todo : password == null : 유머게시판 , password != null : 익명게시판
    fun insertCommentWriteIn(contentUid:String, commentText:String,password:String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val key = commentRef.push().key

            val comment = Comment(contentUid,key,auth.displayName,null,commentText)

            commentRef.child(contentUid).child(key!!).setValue(comment).addOnCompleteListener {
                Log.d("abcd","댓글 작성이 완료 되었습니다.")
            }

        }

    }
}






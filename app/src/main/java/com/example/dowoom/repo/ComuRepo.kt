package com.example.dowoom.repo

import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.User
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

class ComuRepo {


    /** 익명게시판 콘텐츠 리스트 */
    fun getGuestList() : LiveData<MutableList<ComuModel>> {

        val mutableData = MutableLiveData<MutableList<ComuModel>>()
        val guestList:MutableList<ComuModel> = mutableListOf<ComuModel>()

        CoroutineScope(Dispatchers.IO).launch {

            Ref().guestRef().addChildEventListener(object : ChildEventListener {
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
    fun getComments(comuUid: String) : LiveData<MutableList<Comment>> {

        val mutableData = MutableLiveData<MutableList<Comment>>()
        val commentList:MutableList<Comment> = mutableListOf<Comment>()

        Ref().commentRef().child(comuUid).addChildEventListener(object : ChildEventListener {
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

    /** 익명게시판 글 쓰기 */

    fun insertGuestWriteIn(subject: String, content: String) : Flow<Boolean> {
        return flow {
            val comuUid = Ref().guestRef().push().key
            val contentUid = Ref().contentRef().push().key

            // 게시판
            val comuModel = ComuModel(comuUid,subject,2,0,Ref().auth.uid,false, null,content,0,0)

            //내용 task
            val taskTwo = Ref().contentRef().child(comuUid!!).setValue(comuModel)
            //익게 task
            val task = Ref().guestRef().child(comuUid).setValue(comuModel).continueWithTask { task ->
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
    //todo : password == null : 유머게시판 , password != null : 익명게시판
    fun insertCommentWriteIn(contentUid:String, commentText:String,kindOf:Int?,user: User) {
        CoroutineScope(Dispatchers.IO).launch {

            var comment:Comment? = null

            val key = Ref().commentRef().push().key

            comment = if (kindOf == 1) { //유머 게시판
                Comment(contentUid,key,user.nickname,null,commentText)
            } else {
                Comment(contentUid,key,user.guestId,null,commentText)

            }

            Ref().commentRef().child(contentUid).child(key!!).setValue(comment).addOnCompleteListener {
                Log.d("abcd","댓글 작성이 완료 되었습니다.")
            }


        }

    }
}






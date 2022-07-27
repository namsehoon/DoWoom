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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class ComuRepo {

    /** 신고 및 건의 게시판 */
    fun getPoliceList() : LiveData<MutableList<ComuModel>> {
        val mutableData = MutableLiveData<MutableList<ComuModel>>()
        val policeList:MutableList<ComuModel> = mutableListOf<ComuModel>()

        CoroutineScope(Dispatchers.IO).launch {
            Ref().policeRef().child(Ref().auth.uid).addChildEventListener(object : ChildEventListener{

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()) {
                        Log.d("abcd","ComuRepo - getPoliceList - onChildAdded : ${snapshot.ref}")
                        val getPolice = snapshot.getValue(ComuModel::class.java)
                        Log.d("Abcd","getPolice is ; ${getPolice}")
                        policeList.add(0,getPolice!!)

                        mutableData.value = policeList
                    } else {
                        Log.d("abcd"," 없음")
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","ComuRepo - getPoliceList - error : ${error.message}")
                }

            })
        }

        return mutableData

    }

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
                        guestList.add(0,getGuest!!)

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

    fun insertGuestWriteIn(subject: String, content: String, guestId:String,kindOf: Int?) : Flow<Boolean> {
        return flow {

            var comuModel:ComuModel? = null
            //익명 게시판
            if (kindOf == 1) {
                val comuUid = Ref().guestRef().push().key
                comuModel = ComuModel(comuUid,subject,1,0,guestId,false, null,content,0,0)

                //익게 task
                val task = Ref().guestRef().child(comuUid!!).setValue(comuModel).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            Log.d("abcd","ComuRepo - insertGuestWriteIn - 게시판 글 작성 실패")
                            throw  it
                        }
                    } else {
                        Log.d("abcd","ComuRepo - insertGuestWriteIn 테스크 성공")

                    }

                }
                delay(500)
                if (task.isComplete) {
                    emit(true)
                }
            } else { //신고 및 건의 게시판
                val comuUid = Ref().policeRef().push().key
                comuModel = ComuModel(comuUid,subject,2,0,Ref().auth.displayName,false, null,content,0,0)

                val task = Ref().policeRef().child(Ref().auth.uid).child(comuUid!!).setValue(comuModel).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            Log.d("abcd","ComuRepo - insertGuestWriteIn - 게시판 글 작성 실패")
                            throw  it
                        }
                    } else {
                        Log.d("abcd","ComuRepo - insertGuestWriteIn 테스크 성공")

                    }
                }
                delay(500)
                if (task.isComplete) {
                    emit(true)
                }
            }



        }.flowOn(Dispatchers.IO)
    }

    /** 댓글 작성 */
    //todo : password == null : 유머게시판 , password != null : 익명게시판
    fun insertCommentWriteIn(contentUid:String, commentText:String,kindOf:Int?,user: User) {
        CoroutineScope(Dispatchers.IO).launch {

            var comment:Comment? = null

            val key = Ref().commentRef().push().key

            comment = if (kindOf == 0 || kindOf == 2) { //유머 게시판 또는 건의 및 신고 게시판
                Comment(contentUid,key,user.nickname,null,commentText)
            } else {
                Comment(contentUid,key,user.guestId,null,commentText)

            }

            Ref().commentRef().child(contentUid).child(key!!).setValue(comment).addOnCompleteListener {
                Log.d("abcd","댓글 작성이 완료 되었습니다.")
                Ref().guestRef().child(contentUid).child("commentCount").setValue(ServerValue.increment(1))
            }

        }

    }
}






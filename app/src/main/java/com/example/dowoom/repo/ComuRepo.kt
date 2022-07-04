package com.example.dowoom.repo

import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ComuRepo : repo {


    val guestRef = rootRef.child("Comu/Guest")
    val contentRef = rootRef.child("Content")
    val commentRef = rootRef.child("Comment")


    fun getGuestList() : LiveData<MutableList<ComuModel>> {

        val mutableData = MutableLiveData<MutableList<ComuModel>>()
        val guestList:MutableList<ComuModel> = mutableListOf<ComuModel>()

        CoroutineScope(Dispatchers.IO).launch {

            guestRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(guest: DataSnapshot, previousChildName: String?) {
                    if (guest.exists()) {
                        val getGuest = guest.getValue(ComuModel::class.java)
                        guestList.add(getGuest!!)
                        mutableData.value = guestList
                    }
                }

                override fun onChildChanged(guestList: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(guestList: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(guestList: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd","")
                }

            })
        }

        return mutableData
    }

    /** 게시판 글 쓰기 */

    fun insertGuestWriteIn(subject: String, content: String) : Flow<Boolean> {
        return flow {
            val comuUid = guestRef.push().key
            val contentUid = contentRef.push().key

            //익명 게시판
            val comuModel = ComuModel(comuUid,subject,2,0,auth.uid,false, null)
            //내용
            val contentModel = ContentModel(null,comuUid,contentUid,null,content,0,0)

            //내용 task
            val taskTwo = contentRef.child(comuUid!!).setValue(contentModel)
            //익게 task
            val task = guestRef.child(comuUid).setValue(comuModel).continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.d("abcd","ComuRepo - insertGuestWriteIn")
                    task.exception?.let {
                        Log.d("abcd","게시판 글 작성 실패")
                        throw  it
                    }
                }
                taskTwo
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("abcd","게시판 글 작성 성공")
                }
            }

            //todo : task 관리 해야함.. delay?.. runblocking?
            kotlinx.coroutines.delay(500)
            if (task.isSuccessful) {
                emit(true)
            } else {
                emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }
}
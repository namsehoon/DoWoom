package com.example.dowoom.repo

import android.util.Log
import androidx.datastore.preferences.protobuf.StructOrBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.model.Connect
import com.example.dowoom.model.User
import com.example.dowoom.viewmodel.registervm.LoadingViewmodel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


/**
1.Livedata: 식별가능한 데이터 홀더 클래스. 생명주기를 인식, activity fragment 등 생명주기를 고려
2.뷰의 변경 사항을 관찰하고 ACTIVE일 경우에만 뷰를 업데이트하는 데 사용된다.
3.LiveData는 Observer 패턴을 기반으로하며 ViewModel과 View 간의 통신을 쉽게 한다.
4.데이터 변경을 관찰하고 데이터를 자동으로 업데이트한다.
 **/
class userRepo {

    //firebase 인스턴스 및 참조
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val auth:FirebaseUser? = Firebase.auth.currentUser
    val rootRef : DatabaseReference = database.reference
    val myRef: DatabaseReference = rootRef.child("User")


    /** 유저 phone number(id) 가져오기 */
    suspend fun getfireabseUserId() : Flow<String> {
        var phoneNum = ""
        return flow {
            val auth = Firebase.auth
            val firebaseUser: FirebaseUser? = auth.currentUser
            if (firebaseUser?.phoneNumber != null) {
                phoneNum = firebaseUser.phoneNumber.toString()
            } else {
                phoneNum = ""
                Log.d("abcd", "사용자의 폰 번호를 찾을 수 없습니다. : "+phoneNum)
            }
            delay(500)
            emit(phoneNum)

        }.flowOn(Dispatchers.IO)
    }




    /** 온라인 유저 불러오기 */ //todo : childeventlisener로 바꾸고, 이게 최선인지 고민해보기 (callback), 차단한사람 안보여야함
     fun getData(): LiveData<MutableList<User>> {
        // livedata 객체 만들기
        //firebase 추가 된 데이터 이벤트 리스너

        val mutableData = MutableLiveData<MutableList<User>>()
        val listData: MutableList<User> = mutableListOf<User>()
        var index = 0

        //자식 추가 ()
        rootRef.child("Connect").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(connects: DataSnapshot, previousChildName: String?) {
                if (connects.exists()) {
                        //유저
                        Log.d("abcd"," connects.child : "+connects.ref)

                        connects.children.forEach { child ->
                            if (child.value == true) {
                                Log.d("abcd", "child.key is : " + connects.key)
                                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(users: DataSnapshot) {
                                        if (users.exists()) {
                                            val getData = users.child(connects.key!!).getValue(User::class.java)
                                            if (getData?.uid == connects.key!!) {
                                                listData.add(getData)
                                                }
                                            mutableData.value = listData
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                       Log.d("abcd","error in child add is : "+error.message)
                                    }

                                })
                            }
                        }

                } else {
                    Log.d("abcd","snap이 존재하지 않음")
                }
            }
            //자식 변경 (온라인 오프라인)
            override fun onChildChanged(connects: DataSnapshot, previousChildName: String?) {
                if (connects.exists()) {
                    //유저
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(users: DataSnapshot) {
                            val getData = users.child(connects.key!!).getValue(User::class.java)
                            index = listData.indexOf(getData)
                            if (connects.child("connected").value == true) {
                                listData.add(getData!!)
                            } else {
                                listData.removeAt(index)
                            }
                            mutableData.value = listData
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("abcd","error in child add is : "+error.message)
                        }

                    })

                } else {
                    Log.d("abcd","snap이 존재하지 않음")
                }
            }
            //자식 제거 (..계정 삭제?)
            override fun onChildRemoved(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    Log.d("abcd"," result 3 : "+s.value)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                for (s in snapshot.children) {
                    Log.d("abcd"," result 4 : "+s.value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd"," result 5 : "+error.message)

            }

        })


        return mutableData
    }

    /** 자동 로그인 */ //todo : singleLiveEvent로 변경
    suspend fun autoLogin() : Flow<Boolean> {
        return flow {
            var logined = false
            val user = auth
            if (user != null) {
                logined = true
            }
            delay(500)
            emit(logined)
        }.flowOn(Dispatchers.Main)
    }

    /** 닉네임 체크 */
    suspend fun checkData(nickname:String) : Flow<Boolean> {

        //유저들 -> 하위노드 (유저) -> nickname -> 값이 nickname과 같은 쿼리
        return flow {
            var result = false
            val query = myRef.orderByChild("nickname").equalTo(nickname)

               query.get().addOnSuccessListener {
                   Log.d("abcd", "Got value ${it.value}")
                   //if 문
                   if(it.value == null) {
                       result = true
                       Log.d("abcd","사용 할 수 있습니다.")
                   } else {
                       result = false
                   }
                   Log.d("Abcd", "userrepo result is : "+result)
               }.addOnFailureListener{
                   Log.d("abcd", "Error getting data", it)
               }
            delay(500)
            emit(result)

        }.flowOn(Dispatchers.IO)


    }

    /** insert new user */
    suspend fun insertNewUser(uid:String, number: String, nickname: String,stateMsg:String,sOrB:Boolean) : Flow<Boolean> {

        return flow {
            var result = false
                myRef.addListenerForSingleValueEvent(object : ValueEventListener  {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val user = User(uid,0,nickname,stateMsg,0,false,null,sOrB)
                            myRef.child(uid).setValue(user)
                            Log.d("abcd","사용자가 추가 되었습니다. in userrepo")
                            result = true
                        }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("abcd", "error in user repo : "+error.message)
                    }
                })
            delay(500)
            this.emit(result)
        }.flowOn(Dispatchers.IO)
    }
}
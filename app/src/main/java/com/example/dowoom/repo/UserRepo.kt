package com.example.dowoom.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.model.gameModel.GameCount
import com.example.dowoom.model.User
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
    val conRef: DatabaseReference = rootRef.child("Connect")


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



    //참고 : https://mailmail.tistory.com/44
    /** 온라인 유저 불러오기 */ //todo : 이 func은 온라인과 오프라인만 관리함. userdata 변경에 대한건 관리 안함 : 프로필 activity 할때 생각해보자
     suspend fun getData(): LiveData<MutableList<User>> {
        // livedata 객체 만들기
        //firebase 추가 된 데이터 이벤트 리스너
        val listData: MutableList<User> = mutableListOf<User>()
        val mutableData = MutableLiveData<MutableList<User>>()

        CoroutineScope(Dispatchers.IO).launch {
            conRef.orderByChild("connected").equalTo(true).addChildEventListener(object : ChildEventListener {
                //항목 목록을 검색하거나 항목 목록에 대한 추가를 수신 대기
                override fun onChildAdded(connects: DataSnapshot, previousChildName: String?) {
                    if (connects.exists()) {
                        //유저
                        Log.d("abcd","userRepo - onChildAdded")
                        myRef.child(connects.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(users: DataSnapshot) {
                                if (users.exists()) {
                                    val getData = users.getValue(User::class.java)
                                    //나는 나를 볼 수 없게.
                                    if (!getData?.uid.equals(auth?.uid) && !listData.contains(getData)) {
                                        listData.add(getData!!)
                                    }
                                    listData.reverse()
                                    Log.d("abcd","listdata is empty? : ${listData.isEmpty()}")
                                    mutableData.value = listData
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("abcd","error in child add is : "+error.message)
                            }
                        })


                    } else {
                        Log.d("abcd","snap이 존재하지 않음")
                    }

                }
                //목록의 항목에 대한 변경을 수신 대기
                override fun onChildChanged(connects: DataSnapshot, previousChildName: String?) {
                    if (connects.exists()) {
                        //유저
                            Log.d("abcd","userRepo - onChildChanged")

                        myRef.child(connects.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(users: DataSnapshot) {

                                Log.d("abcd","userRepo - onChildChanged")

                                val getData = users.getValue(User::class.java)

                                //유저가 '나' 이거나, 이미 리스트에 포함 되어있다면 뺌
                                if (!getData?.uid.equals(auth?.uid) && !listData.contains(getData)) {
                                    listData.add(getData!!)
                                } else {
                                    listData.remove(getData)
                                }
                                listData.reverse()
                                Log.d("abcd","listdata is empty? : ${listData}")
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
                //목록의 항목 삭제를 수신 대기
                override fun onChildRemoved(connects: DataSnapshot) {
                    if (connects.exists()) {
                        Log.d("abcd","userRepo - onChildRemoved")


                        myRef.child(connects.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(users: DataSnapshot) {

                                val getData = users.getValue(User::class.java)
                                Log.d("abcd","getdata is : ${getData?.uid!!}")
                                listData.remove(getData)

                                mutableData.value = listData
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("abcd","error in child add is : "+error.message)
                            }

                        })
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    for (s in snapshot.children) {
                        Log.d("abcd","유저 가져오기 onChildMoved in Userrepo is : "+s.value)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("abcd"," result 5 : "+error.message)

                }

            })

        }

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
    suspend fun checkData(nickname:String) : LiveData<Boolean> {
        val mutableData = MutableLiveData<Boolean>()
        //유저들 -> 하위노드 (유저) -> nickname -> 값이 nickname과 같은 쿼리
        CoroutineScope(Dispatchers.IO).launch {
            myRef.orderByChild("nickname").equalTo(nickname).get()
                .addOnCompleteListener {
                    Log.d("abcd","it.result.exists() is : ${it.result.exists()}")
                    mutableData.value = it.result.exists()
                }
                .addOnFailureListener {
                    Log.d("abcd","userepo error ${it.message}")
                }
        }
        return mutableData
    }


    /** insert new user */
    suspend fun insertNewUser(uid:String, number: String, nickname: String,stateMsg:String,sOrB:Boolean) : Flow<Boolean> {

        return flow {
            var result = false
            val profileImg = auth?.photoUrl.toString() ?: null
            val user = User(uid,0,nickname,stateMsg,0,false,null,sOrB,profileImg)
            //게임 카운터
            val userCount = GameCount(uid, 0)
            myRef
                .child(uid)
                .setValue(user)
                .addOnSuccessListener {
                    Log.d("abcd","사용자가 추가 되었습니다. in userrepo")

                    result = true
                }
                .addOnFailureListener{ error ->
                    Log.d("abcd", "error in user repo : "+error.message)
                }
            myRef.child("GameCount").child(uid).setValue(userCount).addOnSuccessListener {
                Log.d("abcd","game count가 추가 되었습니다. in userrepo")
            }
            delay(500)
            this.emit(result)
        }.flowOn(Dispatchers.IO)
    }





}
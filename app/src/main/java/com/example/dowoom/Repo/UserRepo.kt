package com.example.dowoom.Repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.model.Connect
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
    val myRef: DatabaseReference = database.getReference("User")
    val auth:FirebaseUser? = Firebase.auth.currentUser


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


    //1. 실시간으로 유저가 있는지 확인
    //2. user가 온라인이면, online인 유저만 받아줌.
    //3. recyclerview 두개로 나눔.. (reference)


    /*** 온라인 유저 */
    val phone =  "0".plus(auth?.phoneNumber?.substring(3))
    //root -> 온라인확인 -> 유저아이디 -> online(boolean)
    val connections = myRef.child(phone).child("Connect").child("connected")
    //root -> 온라인확인 -> 유저아이디 -> offline(boolean)
    val disConnections = myRef.child(phone).child("Connect").child("lastconnect")
    //root -> 온라인확인
    val infoConnected:DatabaseReference = database.getReference(".info/connected")


    //get all user
     fun getData(): LiveData<MutableList<User>> {
        // livedata 객체 만들기
        //firebase 추가 된 데이터 이벤트 리스너

        val mutableData = MutableLiveData<MutableList<User>>()
        val listData: MutableList<User> = mutableListOf<User>()

        infoConnected.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)
                if (connected == true) {
                    connections.setValue(true)
                    connections.onDisconnect().setValue(false)
                    myRef.get().addOnCompleteListener { snapshot
                        ??
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("abcd","error in connection is : "+error.message)
            }

        })



        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //중복 출력 방지
                    listData.clear()
                    for (userData in snapshot.children){
                        // 각 유저를 가져와서, list에 넣어줌.
                        val getData = userData.getValue(User::class.java)
                            if (getData?.Connect?.connected == true) {
                                listData.add(getData)
                            }
                    }
                    //관찰 할 데이터 setvalu를 이용해서 유저를 넣어준 후, return
                    mutableData.value = listData
                }
            }


            //오류가 발생 하였을 때
            override fun onCancelled(error: DatabaseError) {
               Log.d("abcd","에러 : "+error.message)
            }
        })
        return mutableData
    }

    //자동로그인
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

    //이미 사용하고 있는 닉넹미ㅇ
    suspend fun checkData(nickname:String) : Flow<Boolean> {

        //유저들 -> 하위노드 (유저) -> nickname -> 값이 nickname과 같은 쿼리
        return flow {
            var result = false
            val query = myRef.orderByChild("nickname").equalTo(nickname)
           withContext(Dispatchers.IO) {
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
           }
            delay(500)
            emit(result)

        }.flowOn(Dispatchers.IO)


    }

    //insert new user
    suspend fun insertNewUser(number: String, nickname: String,stateMsg:String,sOrB:Boolean) : Flow<Boolean> {

        return flow {
            var result = false
            val connect: Connect // todo 바꿔
            connect = Connect(false)
                myRef.addListenerForSingleValueEvent(object : ValueEventListener  {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val user = User(0,nickname,stateMsg,0,false,null,sOrB, connect)
                            myRef.child(number).setValue(user)
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
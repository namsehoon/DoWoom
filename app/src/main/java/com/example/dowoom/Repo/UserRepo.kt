package com.example.dowoom.Repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
 * */
class userRepo {

    //firebase 인스턴스 및 참조
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.getReference("User")
    val auth:FirebaseUser? = Firebase.auth.currentUser



    //get all user
     fun getData(): LiveData<MutableList<User>> {
        // livedata 객체 만들기
        //firebase 추가 된 데이터 이벤트 리스너
        val mutableData = MutableLiveData<MutableList<User>>()
        val listData: MutableList<User> = mutableListOf<User>()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    listData.clear() // 중복 출력 방지
                    for (userData in snapshot.children){

                        // 각 유저를 가져와서, list에 넣어줌.
                        val getData = userData.getValue(User::class.java)
                        listData.add(getData!!)

                    }

                    //관찰 할 데이터 setvalu를 이용해서 유저를 넣어준 후, return
                    mutableData.value = listData
                }
            }
            //user객체가 들어갈 수 있는 변할 수 있는 mutable 리스트 빈 객체 생성

            //todo 여기부터
//            0. 데이터를 가져올거임 실시간으로 그래서 childlistener에 대해서 공부
//            1. 온라인되어있는 유저
//            2. 뿌려줌

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
                myRef.addListenerForSingleValueEvent(object : ValueEventListener  {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val user = User(0,nickname,stateMsg,0,false,null,sOrB)
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
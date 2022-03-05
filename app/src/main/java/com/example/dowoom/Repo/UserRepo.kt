package com.example.dowoom.Repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.Model.User
import com.google.firebase.database.*


/**
1.Livedata: 식별가능한 데이터 홀더 클래스. 생명주기를 인식, activity fragment 등 생명주기를 고려
2.뷰의 변경 사항을 관찰하고 ACTIVE일 경우에만 뷰를 업데이트하는 데 사용된다.
3.LiveData는 Observer 패턴을 기반으로하며 ViewModel과 View 간의 통신을 쉽게 한다.
4.데이터 변경을 관찰하고 데이터를 자동으로 업데이트한다.
 * */
class userRepo {

    val mutableData = MutableLiveData<MutableList<User>>()

    //firebase 인스턴스 및 참조
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

     fun getData(): LiveData<MutableList<User>> {
        // livedata 객체 만들기
        val myRef: DatabaseReference = database.getReference("User")
        //firebase 추가 된 데이터 이벤트 리스너
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            //user객체가 들어갈 수 있는 변할 수 있는 mutable 리스트 빈 객체 생성
            val listData: MutableList<User> = mutableListOf<User>()
            //데이터가 변할 시, 이벤트
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        // 각 유저를 가져와서, list에 넣어줌.
                        val getData = userSnapshot.getValue(User::class.java)
                        listData.add(getData!!)

//
                        //관찰 할 데이터 setvalu를 이용해서 유저를 넣어준 후, return
                        mutableData.value = listData
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
               Log.d("abcd","에러 : "+error)
            }
        })
        return mutableData
    }
}
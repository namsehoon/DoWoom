package com.example.dowoom.viewmodel.registerViewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CheckViewmodel : ViewModel() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.getReference("User")

    fun checkData(nickname:String) : LiveData<Boolean>  {

        //유저들 -> 하위노드 (유저) -> nickname -> 값이 nickname과 같은 쿼리

        val mutableData = MutableLiveData<Boolean>()
        val query = myRef.orderByChild("nickname").equalTo(nickname)

        //유저들 -> 하위노드 (유저) -> nickname -> 값이 nickname과 같은 쿼리
        query.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            //if 문
            if(it.value == null) {
                mutableData.value = true
                Log.d("abcd","사용 할 수 있습니다.")
            } else {
                mutableData.value = false
            }
            Log.d("abcd","mutable is : "+mutableData.value)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        return mutableData

    }

    override fun onCleared() {
        super.onCleared()

    }

}


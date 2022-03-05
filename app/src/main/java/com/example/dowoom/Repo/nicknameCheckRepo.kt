package com.example.dowoom.Repo

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dowoom.Model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import kotlin.coroutines.coroutineContext

class nicknameCheckRepo {


    //db
    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.getReference("User")

     fun checkData(nickname:String) : LiveData<Boolean>  {

        val mutableData = MutableLiveData<Boolean>()


        //유저들 -> 하위노드 (유저) -> nickname -> 값이 nickname과 같은 쿼리
        myRef.orderByChild("nickname").equalTo(nickname).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            if(it.value != null) {
                mutableData.value = false
            } else {
                mutableData.value = true
                Log.d("abcd","사용 할 수 있습니다.")
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        return mutableData

    }
}
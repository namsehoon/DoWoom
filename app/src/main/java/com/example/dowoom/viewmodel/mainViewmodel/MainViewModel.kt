package com.example.dowoom.viewmodel.mainViewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.User
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.viewmodel.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {

    val auth: FirebaseAuth
        get() = Firebase.auth


    //로그아웃
    fun logout() {
        auth.signOut()
    }

    //현재 유저 있는지

    private val _hasUser = MutableLiveData<Boolean>()
    val hasUser : LiveData<Boolean>
        get() = _hasUser

    fun observeHasCurrentUser() {
        _hasUser.value = auth.currentUser != null
    }


    /** 현재 로그인된 유저 */
    private val _currentUser = MutableLiveData<User>()
    val currentUser : LiveData<User>
        get() = _currentUser


    fun getUserInfo()  {
        Ref().userRef().child(Ref().auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    _currentUser.value = user!!

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("abcd","MainViewModel - getUserInfo - error : ${error.message}")
            }

        })
    }
}
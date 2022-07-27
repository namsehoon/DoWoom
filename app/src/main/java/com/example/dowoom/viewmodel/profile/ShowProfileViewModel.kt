package com.example.dowoom.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.User
import com.example.dowoom.repo.userRepo

class ShowProfileViewModel : ViewModel() {

    val repo = userRepo()

    /** 차단하기 */
    fun blockUser(partnerUid:String) {
        repo.blockUser(partnerUid)
    }


    val _blockCheck = MutableLiveData<Boolean>()
    val blockCheck : LiveData<Boolean> get() = _blockCheck

    /** 차단 관찰 */
    fun observeBlock(partnerUid: String) {
        repo.observeBlock(partnerUid).observeForever(Observer {
            _blockCheck.value = it
        })
    }
    /** 내 정보 가져오기 */
    val _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user


    fun getUserInfo() {
        repo.observeUser(Ref().auth.uid).observeForever(Observer {
            _user.value = it
        })
    }
}
package com.example.dowoom.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.User
import com.example.dowoom.repo.userRepo

class ChangeProfileViewModel : ViewModel() {

    val repo = userRepo()

    /** 내 정보 가져오기 */
    val _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user


    fun getUserInfo() {
        repo.observeUser(Ref().auth.uid).observeForever(Observer {
            if (it != null) {
                _user.value = it
            }
        })
    }

    /** 프로필 업데이트 */

    val _completed = MutableLiveData<Boolean>()
    val completed: LiveData<Boolean> get() = _completed

    fun updateUserProfile(statusMsg:String) {
        repo.updateUserProfile(statusMsg).observeForever(Observer {
            if (it != null) {
                _completed.value = it
            }
        })
    }
}
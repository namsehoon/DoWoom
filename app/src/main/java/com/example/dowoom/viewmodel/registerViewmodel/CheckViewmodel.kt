package com.example.dowoom.viewmodel.registerViewmodel

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.Model.User
import com.example.dowoom.Repo.nicknameCheckRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CheckViewmodel : ViewModel() {

    //닉네임을 넣으면 boolean을 리턴한다. nickname을 observe 해야한다.
    private val repo = nicknameCheckRepo()


    //viewmodel -> repository를 보고 있음
    //1. nickname 중복여부를 관찰(repo), 2.중복이 안되면 다음으로 넘어가게끔
     fun getResult(nickname:String) : LiveData<Boolean> {
        val mutableData = MutableLiveData<Boolean>()
        repo.checkData(nickname).observeForever {
            mutableData.postValue(it)
        }
        return mutableData
    }


}
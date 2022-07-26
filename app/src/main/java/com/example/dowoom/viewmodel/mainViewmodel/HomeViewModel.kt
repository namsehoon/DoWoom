package com.example.dowoom.viewmodel.mainViewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.model.User
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.repo.ChatRepo
import com.example.dowoom.repo.userRepo
import com.example.dowoom.viewmodel.SingleLiveEvent
import com.google.firebase.database.core.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch


/**
 * LiveData는 ViewModel에서 사용되도록 설계가 되었다.
 * 액티비티나 프래그먼트가 재실행되도 ViewModel은 소멸되지 않기 때문에 LiveData도 소멸되지 않는다.
 * 따라서 메모리릭 등의 문제를 신경쓰지 않아도 된다.
 */

class HomeViewModel : ViewModel() {
    //유저 가져옴
    private val userRepo = userRepo()

    init {
        viewModelScope.launch {
            observeUser()
        }
    }
    /** 유저 관찰 */

    private val _userList = MutableLiveData<MutableList<User>>()
    val userList: LiveData<MutableList<User>>
        get() = _userList

    suspend fun observeUser() {
        userRepo.getData().observeForever(Observer { it ->
            _userList.value = it
            _progress.value = false
        })
    }

    /** 프로세스(새로고침) */
    private val _progress = MutableLiveData<Boolean>(true)
    val progress: LiveData<Boolean>
        get() = _progress



}
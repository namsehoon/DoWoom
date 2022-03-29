package com.example.dowoom.viewmodel.mainViewmodel

import androidx.lifecycle.*
import com.example.dowoom.model.User
import com.example.dowoom.repo.userRepo


/**
 * LiveData는 ViewModel에서 사용되도록 설계가 되었다.
 * 액티비티나 프래그먼트가 재실행되도 ViewModel은 소멸되지 않기 때문에 LiveData도 소멸되지 않는다.
 * 따라서 메모리릭 등의 문제를 신경쓰지 않아도 된다.
 */

class HomeViewModel : ViewModel() {
    //유저 가져옴
    private val repo = userRepo()




    suspend fun observeUser() : LiveData<MutableList<User>> {
        val userList =  MutableLiveData<MutableList<User>>()
            repo.getData().observeForever(Observer { it ->
                userList.value = it

            })



        return userList
    }

}
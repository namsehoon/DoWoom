package com.example.dowoom.viewmodel.registervm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.Repo.userRepo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadingViewmodel: ViewModel() {

    val repo = userRepo()

    //현재 유저가 있으면 -> 바로 메인으로
    private val _autoLogin = MutableLiveData<Boolean>()

    //view에서 사용
    val autoLogin: LiveData<Boolean> get() = _autoLogin.apply {
        viewModelScope.launch {
            repo.autoLogin().catch { e->
                Log.d("abcd"," auto login error is : "+e)

            }
                .collect {
                    _autoLogin.value = it
                }
        }
    }

//    fun autoLoginfun() : LiveData<Boolean> {
//        viewModelScope.launch {
//            repo.autoLogin().catch { e->
//                Log.d("abcd"," auto login error is : "+e)
//
//            }
//                .collect {
//                _autoLogin.value = it
//            }
//        }
//    }

}
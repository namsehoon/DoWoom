package com.example.dowoom.viewmodel.registervm

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.repo.userRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CheckViewmodel : ViewModel() {

    private val repo = userRepo()


    //닉네임
    var etNickname = MutableLiveData<String>("")
    //상태메세지
    var etStateMsg = MutableLiveData<String>("")



    //닉네임 확인
    private val _requestOkOrNot = MutableLiveData<Boolean>()
    private var repoResult = false

    //view에서 사용 할.
    val requestOkOrNot :LiveData<Boolean> get() = _requestOkOrNot


    fun requestOkOrNotFun() {
        viewModelScope.launch {
            Log.d("Abcd","etNickname : "+etNickname.value)
            if (!etNickname.value.isNullOrBlank()) {
                repo.checkData(etNickname.value!!)
                    .catch { e ->
                        Log.d("abcd", "error in checkviewmodel is :" + e.message)
                    }.collect {
                        _requestOkOrNot.value = it
                    }
            } else {
                Log.d("abcd", "닉네임을 입력 해주세요.")
            }
        }

    }
}


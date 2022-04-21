package com.example.dowoom.viewmodel.registervm

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.repo.userRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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


    suspend fun nicknameAvailable()  {
        viewModelScope.launch {
            if (!etNickname.value?.isNullOrEmpty()!!) {
                Log.d("Abcd","etNickname : "+etNickname.value)
                val nickname = etNickname.value.toString()
                repo.checkData(nickname).observeForever { result ->
                    _requestOkOrNot.value = result
                }
            } else {
                Log.d("abcd", "닉네임을 입력 해주세요.")
            }
        }
    }



    //insert
    private val _insertComplete = MutableLiveData<Boolean>()


    //todo = 사용자가 이미 존재해도 데이터 업데이트 됨.
    suspend fun userInsert(uid:String, phoneNum:String,nickname:String,statusMsg:String,sOrB:Boolean) {
        viewModelScope.launch {
            repo.insertNewUser(uid,phoneNum,nickname,statusMsg,sOrB)
                .catch {  e ->
                    Log.d("abcd", "error in reguster viewmodel is :" + e.message)
                }.collect {
                    _insertComplete.value = it
                }

        }
    }
}


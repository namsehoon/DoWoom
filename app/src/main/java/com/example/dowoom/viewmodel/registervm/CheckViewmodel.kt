package com.example.dowoom.viewmodel.registervm

import android.app.backup.BackupAgent
import android.util.Log
import androidx.databinding.ObservableField
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
    var etNickname = ObservableField<String>("")
    //상태메세지
    var etStateMsg = ObservableField<String>("")



    //닉네임 확인
    private val _requestOkOrNot = MutableLiveData<Boolean>()

    //view에서 사용 할.
    val requestOkOrNot :LiveData<Boolean> get() = _requestOkOrNot


    suspend fun nicknameAvailable()  {
        viewModelScope.launch {
            if (!etNickname.get().toString().isNullOrEmpty()) {
                Log.d("Abcd","etNickname : "+etNickname.get().toString())
                val nickname = etNickname.get().toString()
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
    val insertComplete:LiveData<Boolean> get() = _insertComplete

    suspend fun userInsert(statusMsg:String,sOrB:Boolean,age:Int,birthday:Int) {
        viewModelScope.launch {
            if (!etNickname.get().toString().isNullOrEmpty()) {
                Log.d("abcd","nickname in viewmodel is : ${etNickname.get().toString()}")
                repo.insertNewUser(etNickname.get().toString(),statusMsg,sOrB,age,birthday).observeForever(
                    Observer {
                        _insertComplete.value = it
                    })
            }

        }
    }
}


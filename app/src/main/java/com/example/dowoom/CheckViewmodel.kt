package com.example.dowoom

import android.util.Log
import androidx.lifecycle.*
import com.example.dowoom.Repo.userRepo
import com.example.dowoom.viewmodel.SingleLiveEvent
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
    //다음으로 넘어가기
    private val _goTo = SingleLiveEvent<Unit>()

    //이전으로 돌아가기
    private val _backTo = SingleLiveEvent<Unit>()

    //view에서 사용 할.
    val requestOkOrNot :LiveData<Boolean> get() = _requestOkOrNot
    val goTo:LiveData<Unit> get() = _goTo
    val backTo:LiveData<Unit> get() = _backTo

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


package com.example.dowoom.viewmodel.registervm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.repo.userRepo
import com.example.dowoom.viewmodel.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewmodel : ViewModel() {

    val repo = userRepo()

    // 인증번호 다시 보내기
    var isResendPhoneAuth: Boolean = false
    //editText 폰 번호
    val etPhoneNum = MutableLiveData<String>("")
    //editText 인증 번호
    val etAuthNum = MutableLiveData<String>("")

    //인증 요청 , SingleLiveEvent : viewmodel에서 view에 이벤트를 전달할 때, "값"을 전달하는 경우가 아닌 이벤트가 발생했다는 "사실"만을 전달하고 싶을 때.
    private val _requestAuth = MutableLiveData<Boolean>()
    //다시 인증 요청
    private val _requestResendPhoneAuth = MutableLiveData<Boolean>()
    //인증 완료
    private val _authComplete = SingleLiveEvent<Unit>()

    // 이건 view(activity, fragment) 에서 사용
    val requestAuth:LiveData<Boolean> get() = _requestAuth
    val requestResendPhoneAuth:LiveData<Boolean> get() =  _requestResendPhoneAuth
    val authComplete:LiveData<Unit> get() = _authComplete

    //인증 요청 메소드
    fun requestAuth() {
       //첫번째 요청
        if (!isResendPhoneAuth) {
            Log.d("Abcd","폰번호 : "+etPhoneNum.value)
            //editText 폰 번호가 null이나 blank가 아니면 true else false
           _requestAuth.value = !etPhoneNum.value.isNullOrBlank()
       } else {
           //인증 번호 재요청
               Log.d("Abcd","폰번호 : "+etPhoneNum.value)
           _requestResendPhoneAuth.value = !etPhoneNum.value.isNullOrBlank()
       }
    }

    // 인증 완료
    fun authComplete() {
        _authComplete.call()
    }







}
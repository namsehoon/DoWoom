package com.example.dowoom.viewmodel.registerViewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dowoom.viewmodel.BaseViewModel
import com.example.dowoom.viewmodel.SingleLiveEvent

class RegisterViewmodel : BaseViewModel() {

    //인증번호
    lateinit var authNum: String
    //휴대폰 번호
    var tel:String = ""
    //editText 폰 번호
    val etPhoneNum = MutableLiveData<String>("")
    //editText 인증 번호
    val etAuthNum = MutableLiveData<String>("")

    //인증 요청 , SingleLiveEvent : viewmodel에서 view에 이벤트를 전달할 때, "값"을 전달하는 경우가 아닌 이벤트가 발생했다는 "사실"만을 전달하고 싶을 때.
    private val _requestAuth = SingleLiveEvent<Boolean>()
    //인증 요청 유무
    private val _authState = MutableLiveData<Boolean>()
    //유저 인증 결과
    private val _resultAuthUser = MutableLiveData<Boolean>()

    //wrapping (todo check : 이건 view에서 사용?)
    val requestAuth:LiveData<Boolean> get() = _requestAuth
    val authState:LiveData<Boolean> get() =  _authState
    val resultAuthUser:LiveData<Boolean> get() = _resultAuthUser

    //인증 요청 메소드
    fun requestAuth() {
        //인증요청 null 또는 공백이 아닌 경우 return true else false
        _requestAuth.value = !etPhoneNum.value.isNullOrBlank()
    }

    // 인증 상태 업데이트 메소드
    fun updateAuthState(boolean: Boolean) {
        //인증 상태 boolean
        _authState.value = boolean
        // 변수에 폰 번호를 넣어줌
        tel = etPhoneNum.value.toString()
    }

    fun authUser() {
        // 인증 번호가 할당 되었으면 return true else false, "받은" 인증번호와 "입력" 인증번호가 같은 경우 return true else false
        if(this::authNum.isInitialized && authNum == etAuthNum.value.toString()) {
            updateUserTel()
        } else {
            _resultAuthUser.value = false
        }
    }

    private fun updateUserTel() {
        // add : 구독(관찰객체에 추가)
    }


}
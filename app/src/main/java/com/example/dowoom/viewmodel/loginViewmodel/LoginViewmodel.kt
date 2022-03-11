package com.example.dowoom.viewmodel.loginViewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.dowoom.viewmodel.BaseViewModel

//로그인 뷰모델
class LoginViewmodel : BaseViewModel() {

    //폰 번호
    var phone = ObservableField<String>()

    // 인증번호 받기
    fun onClickGetAuth() {
        //1. 버튼을 누르면 인증번호가 폰으로 전송된다.
        //2. 인증번호를 입력할 수 있는 input이 생성된다.
    }

    // 인증 후 main으로 이동
    fun onClickCompleteAuth() {
        //1. 서버에서 보낸 인증번호를 확인 한다.
        //2. 인증번호가 틀리면 틀렸다고 메세지를 보낸다.
        //3. 인증번호가 맞으면 main으로 보낸다.
    }

}
//todo
//https://velog.io/@hanna2100/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%B0%94%EC%9D%B8%EB%94%A9
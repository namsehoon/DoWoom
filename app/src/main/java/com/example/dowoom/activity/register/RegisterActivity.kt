package com.example.dowoom.activity.register

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.R
import com.example.dowoom.databinding.ActivityRegisterBinding
import com.example.dowoom.viewmodel.registerViewmodel.CheckViewmodel
import com.example.dowoom.viewmodel.registerViewmodel.RegisterViewmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.dowoom.Repo.userRepo

//1. 인증문자 보내기
//2. Callbacks 구현 (성공, 실패, )
//3. credential
//4. Sign in (startactivity to main), create user
class RegisterActivity(private val resporitory:userRepo) : BaseActivity<ActivityRegisterBinding>(TAG = "RegisterActivity", R.layout.activity_register) {

    private lateinit var auth:FirebaseAuth
    private lateinit var viewmodel:RegisterViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialized()
    }


    fun initialized() {
        //auth
        auth = Firebase.auth
        //viewmodel
        viewmodel = ViewModelProvider(this).get(RegisterViewmodel::class.java)
        binding.vm = viewmodel

        //viwmodel call back 실행
        initViewmodelCallback()
    }

    private fun initViewmodelCallback() {
        //with : 수신객체, 수신객체 지정 람다.
        with(viewmodel) {
            resporitory
        }
    }
}

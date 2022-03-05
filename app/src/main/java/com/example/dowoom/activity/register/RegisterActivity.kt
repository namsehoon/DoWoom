package com.example.dowoom.activity.register

import android.os.Bundle
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.R
import com.example.dowoom.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(TAG = "RegisterActivity", R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo 여기부터 authenticated 폰 인증으로 만들어야 됨.
    }
}
package com.example.dowoom.activity.login

import android.os.Bundle
import androidx.activity.viewModels
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.R
import com.example.dowoom.databinding.ActivityLoginBinding
import com.example.dowoom.viewmodel.loginViewmodel.LoginViewmodel

class LoginActivity : BaseActivity<ActivityLoginBinding>(TAG = "LoginActivity", R.layout.activity_login) {

    val viewModel: LoginViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}
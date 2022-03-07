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

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(TAG = "RegisterActivity", R.layout.activity_register) {

    private lateinit var auth:FirebaseAuth
    private lateinit var viewmodel:RegisterViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //auth
        auth = Firebase.auth
        //viewmodel
        viewmodel = ViewModelProvider(this).get(RegisterViewmodel::class.java)
        binding.vm = viewmodel
    }
}
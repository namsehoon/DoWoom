package com.example.dowoom.activity.login

import android.os.Bundle
import androidx.activity.viewModels
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.viewmodel.registervm.CheckActivity
import com.example.dowoom.databinding.ActivityStartBinding
import com.example.dowoom.activity.register.RegisterActivity
import com.example.dowoom.viewmodel.registervm.StartViewmodel

class StartActivity : BaseActivity<ActivityStartBinding>(TAG = "StartActivity", R.layout.activity_start) {

     val viewModel: StartViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        //여기서 permission ?

        binding.startBtn.setOnClickListener {
            startNextActivity(CheckActivity::class.java)
            finish()
        }

        binding.loginBtn.setOnClickListener {
            startNextActivity(RegisterActivity::class.java)
            finish()
        }
    }
}
package com.example.dowoom.activity.login

import android.os.Bundle
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.activity.register.CheckActivity
import com.example.dowoom.databinding.ActivityStartBinding
import com.example.dowoom.activity.register.RegisterActivity

class StartActivity : BaseActivity<ActivityStartBinding>(TAG = "StartActivity", R.layout.activity_start) {
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
package com.example.dowoom.activity.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.activity.main.MainActivity
import com.example.dowoom.databinding.ActivityLoadingBinding
import com.example.dowoom.viewmodel.registervm.LoadingViewmodel
import com.google.firebase.auth.FirebaseAuth

class LoadingActivity : BaseActivity<ActivityLoadingBinding>(TAG = "로딩액티비티",R.layout.activity_loading) {

    //뷰모델
    val viewModel : LoadingViewmodel by viewModels()
    //파이어베이스 auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)


        initialized()
    }

    private fun initialized() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.autoLogin.observe(this, Observer {
            if (it) {
                //자동로그인 (current user)
                startNextActivity(MainActivity::class.java)
                finish()
            } else {
                startNextActivity(StartActivity::class.java)
                finish()
            }
        })
    }
}
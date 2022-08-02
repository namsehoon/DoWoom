package com.example.dowoom.activity.login

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.activity.main.MainActivity
import com.example.dowoom.activity.register.CheckActivity
import com.example.dowoom.dataStore.DataStore
import com.example.dowoom.dataStore.DataStoreST
import com.example.dowoom.databinding.ActivityLoadingBinding
import com.example.dowoom.viewmodel.registervm.LoadingViewmodel
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class LoadingActivity : BaseActivity<ActivityLoadingBinding>(TAG = "로딩액티비티",R.layout.activity_loading) {

    //뷰모델
    //파이어베이스 auth
    private lateinit var auth: FirebaseAuth
    //로딩
    private val LOADING_TIME:Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initialized()
    }


    private fun initialized() {

        Handler().postDelayed({

            //firebase app check
            try {
                FirebaseApp.initializeApp(this)
                val firebaseAppCheck = FirebaseAppCheck.getInstance()
                firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())
            }catch (e:Exception) {
                Log.d("abcd","appCheck Exeption : ${e.message}")
            }


            binding.lifecycleOwner = this
            auth = FirebaseAuth.getInstance()

            //유저 o , 닉네임 o
            if (auth.currentUser != null ) {
                startNextActivity(MainActivity::class.java)
                finish()
                //유저 o , 닉네임 x
            } else {
                startNextActivity(StartActivity::class.java)
                finish()
                //유저 x , 닉네임 x
            }


        }, LOADING_TIME)








    }
}
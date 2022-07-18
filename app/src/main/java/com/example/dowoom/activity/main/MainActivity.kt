package com.example.dowoom.activity.main

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.activity.login.StartActivity
import com.example.dowoom.activity.register.RegisterActivity
import com.example.dowoom.dataStore.DataStoreST
import com.example.dowoom.fragments.*
import com.example.dowoom.databinding.ActivityMainBinding
import com.example.dowoom.model.User
import com.example.dowoom.viewmodel.mainViewmodel.MainViewModel
import com.example.dowoom.viewmodel.registervm.CheckViewmodel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

//baseActivity() 상속 (intent, replaceFragment startNextActivity(클래스::class.java). todo binding 추가 예정)
class MainActivity : BaseActivity<ActivityMainBinding>(TAG = "MainActivity", R.layout.activity_main) {

    lateinit var homeFrag: HomeFrag
    lateinit var gameFrag: GameFrag
    lateinit var talkFrag: TalkFrag
    lateinit var comuFrag: ComuFrag
    lateinit var settingFrag: SettingFrag

    val viewModel : MainViewModel by viewModels()

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        val connect = viewModel.database.reference.child("Connect/${viewModel.auth.uid}/connected")
        connect.setValue(false).addOnFailureListener {
            Log.d("abcd", " it.message is : " + it.message)
        }
    }
    //온라인
    override fun onResume() {
        super.onResume()
        val connect = viewModel.database.reference.child("Connect/${viewModel.auth.uid}/connected")
        connect.setValue(true).addOnFailureListener {
            Log.d("abcd", " it.message is : " + it.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //초기화
        initialized()
        observeUser()


        //텝 리스너
        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            // 선택될 때
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        replaceview(homeFrag)
                    }
                    1 -> {
                        replaceview(talkFrag)
                    }
                    2 -> {
                        replaceview(gameFrag)
                    }
                    3 -> {
                        replaceview(comuFrag)
                    }
                    4 -> {
                        replaceview(settingFrag)
                    }
                }
            }

            // 선택되지 않을 시
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            // 다시 선택 될 때,
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        Log.d("abcd","auth uid in mainac : "+viewModel.auth.uid)
    }

    //시작시에,
    fun initialized() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //유저 정보 가져오기
        viewModel.getUserInfo()

        //로그인 안되어있으면, 회원가입 창으로
        if (viewModel.auth == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        //프레그먼트
        homeFrag = HomeFrag()
        gameFrag = GameFrag()
        comuFrag = ComuFrag()
        talkFrag = TalkFrag()
        settingFrag = SettingFrag()




        //처음 시작시 보여지는 프래그먼트
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, homeFrag).commit()


        setSupportActionBar(findViewById(R.id.toolbarMain))
        supportActionBar?.also {
            //타이틀 사용 여부
            it.setDisplayShowTitleEnabled(false)
        }


        val dataStore = DataStoreST.getInstance(this)
        lifecycleScope.launch {
            dataStore.storeData("as","as")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.notiItem -> {
                Log.d("abcd","notiItem 클릭됨")
                super.onOptionsItemSelected(item)
            }
            R.id.filterItem -> {
                Log.d("abcd","filterItem 클릭됨")
                viewModel.logout()
                super.onOptionsItemSelected(item)
            }
            R.id.settingItem -> {

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun observeUser() {
        viewModel.currentUser.observe(this, Observer {
            comuFrag.user = it //유저 정보 보내기 ()
        })

        viewModel.hasUser.observe(this, Observer { bool ->
            if (!bool) { //유저 로그인 되어 있음
                val intent = intent
                if (viewModel.auth == null && intent.getStringExtra("nickname") == null) {
                    startNextActivity(StartActivity::class.java)
                    finish()
                }
            }

        })
    }


    //fragment 변경
    private fun replaceview(tab:Fragment) {
        //화면변경
        var selectedFragment : Fragment? = null
        selectedFragment = tab
        selectedFragment.let {
            //프레그먼트 변경

            replaceFragment(selectedFragment,selectedFragment::class.simpleName!!)
        }
    }

}
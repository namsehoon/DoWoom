package com.example.dowoom.activity.main

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.activity.login.StartActivity
import com.example.dowoom.activity.register.RegisterActivity
import com.example.dowoom.dataStore.DataStoreST
import com.example.dowoom.fragments.*
import com.example.dowoom.databinding.ActivityMainBinding
import com.example.dowoom.viewmodel.mainViewmodel.MainViewModel
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

    var database: FirebaseDatabase? = null
    var auth: FirebaseUser? = null

    override fun onStart() {
        super.onStart()
        if (auth == null) {
            startNextActivity(StartActivity::class.java)
            finish()
        } else {
            //로그아웃
            Firebase.auth.signOut()
            startNextActivity(StartActivity::class.java)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        val connect = database!!.reference.child("Connect/".plus(auth!!.uid).plus("/connected/"))
        connect.setValue(false).addOnFailureListener {
            Log.d("abcd", " it.message is : " + it.message)
        }
    }

    override fun onResume() {
        super.onResume()
        val connect = database!!.reference.child("Connect/".plus(auth!!.uid).plus("/connected/"))
        connect.setValue(true).addOnFailureListener {
            Log.d("abcd", " it.message is : " + it.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //초기화
        initialized()

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

        Log.d("abcd","auth uid in mainac : "+auth!!.uid)
    }

    //시작시에,
    fun initialized() {

        //로그인 안되어있으면, 회원가입 창으로
        val uid = auth?.uid

        if (uid == null) {
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

         database = Firebase.database

        auth = Firebase.auth.currentUser

        val dataStore = DataStoreST.getInstance(this)
        lifecycleScope.launch {
            dataStore.storeData("as","as")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.notiItem -> {
                Log.d("abcd","notiItem 클릭됨")
                return super.onOptionsItemSelected(item)
            }
            R.id.filterItem -> {
                Log.d("abcd","filterItem 클릭됨")
                return  super.onOptionsItemSelected(item)
            }
            R.id.settingItem -> {
                Log.d("abcd","settingItem 클릭됨")
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }

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
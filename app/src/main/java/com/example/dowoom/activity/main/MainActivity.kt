package com.example.dowoom.activity.main

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.fragments.*
import com.example.dowoom.databinding.ActivityMainBinding
import com.example.dowoom.viewmodel.mainViewmodel.MainViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//baseActivity() 상속 (intent, replaceFragment startNextActivity(클래스::class.java). todo binding 추가 예정)
class MainActivity : BaseActivity<ActivityMainBinding>(TAG = "MainActivity", R.layout.activity_main) {

    lateinit var homeFrag: HomeFrag
    lateinit var gameFrag: GameFrag
    lateinit var talkFrag: TalkFrag
    lateinit var comuFrag: ComuFrag
    lateinit var settingFrag: SettingFrag


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


    }

    //시작시에,
    fun initialized() {

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

        val db = Firebase.database
        val myRef = db.getReference("message")

        myRef.setValue("Hrllo, world")

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
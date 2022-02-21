package com.example.dowoom

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.example.dowoom.Fragments.*
import com.example.dowoom.databinding.ActivityMainBinding

//baseActivity() 상속 (intent, replaceFragment startNextActivity(클래스::class.java). todo binding 추가 예정)
class MainActivity : BaseActivity(TAG = "MainActivity") {


    lateinit var homeFrag: HomeFrag
    lateinit var gameFrag: GameFrag
    lateinit var talkFrag: TalkFrag
    lateinit var comuFrag: ComuFrag
    lateinit var settingFrag: SettingFrag


    //데이터바인딩
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        activityTag = MainActivity::class.simpleName
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //초기화
        initialized()

        //텝 리스너
        binding.tabLayout.addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {

            // 선택될 때
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> { replaceview(homeFrag) }
                    1 -> { replaceview(talkFrag) }
                    2 -> { replaceview(gameFrag) }
                    3 -> { replaceview(comuFrag) }
                    4 -> { replaceview(settingFrag) }
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
        //바인딩
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        //main.xml 정의된 이름.
        binding.activity = this

        //프레그먼트
        homeFrag = HomeFrag()
        gameFrag = GameFrag()
        comuFrag = ComuFrag()
        talkFrag = TalkFrag()
        settingFrag = SettingFrag()

        //처음 시작시 보여지는 프래그먼트
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, homeFrag).commit()

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
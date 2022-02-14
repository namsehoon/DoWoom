package com.example.dowoom

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.dowoom.Fragments.*
import com.example.dowoom.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var homeFrag: HomeFrag
    lateinit var gameFrag: GameFrag
    lateinit var talkFrag: TalkFrag
    lateinit var comuFrag: ComuFrag
    lateinit var settingFrag: SettingFrag


    //데이터바인딩
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //초기화
        initialized()

        //텝 리스너
        binding.tabLayout.addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
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

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //todo : https://jwsoft91.tistory.com/52
                // 그리고 fragment view model 알아보기
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
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

    private fun replaceview(tab:Fragment) {
        //화면변경
        var selectedFragment : Fragment? = null
        selectedFragment = tab
        selectedFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout,it).commit()
        }
    }
}
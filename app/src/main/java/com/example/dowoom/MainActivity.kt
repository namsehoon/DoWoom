package com.example.dowoom

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.Data.dataModel.User
import com.example.dowoom.DataStore.DataStore
import com.example.dowoom.DataStore.DataStoreST
import com.example.dowoom.fragments.*
import com.example.dowoom.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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

//        //todo : 여기부터
//        val connectedRef = Firebase.database.getReference(".info/connected")
//        connectedRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(User::class.java) ?: false
//                if (connected) {
//                    Log.d("abcd","연결됨")
//                } else {
//                    Log.d("abcd", "연결끊김")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("abcd","취소됨")
//            }
//        })

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
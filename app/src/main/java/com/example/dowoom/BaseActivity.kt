package com.example.dowoom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.dowoom.DataStore.DataStoreST

//생명주기 관리를 위한, 1.데이터 바인
abstract class BaseActivity<T: ViewDataBinding>(TAG:String? = null, @LayoutRes private val layoutRes: Int) : AppCompatActivity() {

    //태그
    var activityTag:String? = null
    //2.데이터 바인딩
    protected  lateinit var binding:T

    init {
        this.activityTag = TAG

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(activityTag,  activityTag + " onCreate")
        super.onCreate(savedInstanceState)
        //3.데이터 바인딩
        binding = DataBindingUtil.setContentView(this,layoutRes)

    }


    //프레그먼트 태그

    //프레그먼트 변경
    fun replaceFragment(fragment: Fragment, tag: String) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment, tag).commit()
    }

    //액티비티 이동
    fun startNextActivity(className:Class<*>) {
        var intent = Intent(this, className)
        startActivity(intent)
    }

    //생명주기
    override fun onStart() {
        Log.d(activityTag,  activityTag + " onStart")
        super.onStart()
    }

    override fun onRestart() {
        Log.d(activityTag,  activityTag + " onRestart")
        super.onRestart()
    }

    override fun onResume() {
        Log.d(activityTag,  activityTag + " onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(activityTag,  activityTag + " onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(activityTag,  activityTag + " onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(activityTag,  activityTag + " onDestroy")
        super.onDestroy()
    }


}
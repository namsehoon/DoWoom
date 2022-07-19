package com.example.dowoom.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.dowoom.R
import com.example.dowoom.viewmodel.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import android.widget.EditText




//생명주기 관리를 위한, 1.데이터 바인
abstract class BaseActivity<T: ViewDataBinding>(TAG:String? = null, @LayoutRes private val layoutRes: Int) : AppCompatActivity() {

    //태그
    var activityTag:String? = null
    //2.데이터 바인딩
    protected lateinit var binding:T

    init {
        this.activityTag = TAG

    }

    //edit text 다른 화면 터치시, 키보드 내려감
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view: View? = currentFocus
        if (view != null && (ev!!.action === MotionEvent.ACTION_UP || ev!!.action === MotionEvent.ACTION_MOVE) && view is EditText && !view::class.java
                .name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev!!.rawX + view.getLeft() - scrcoords[0]
            val y: Float = ev!!.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(activityTag,  activityTag + " onCreate")
        super.onCreate(savedInstanceState)
        //3.데이터 바인딩
        binding = DataBindingUtil.setContentView(this,layoutRes)


    }
    //토스트 메시지
    fun showToast(message:String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
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
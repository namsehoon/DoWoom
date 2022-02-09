package com.example.dowoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.dowoom.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    //데이터바인딩
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //초기화
        initialized()


    }

    fun initialized() {
        //바인딩
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        //main.xml 정의된 이름.
        binding.activity = this

    }
}
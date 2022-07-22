package com.example.dowoom.activity.comu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityGuestWriteBinding
import com.example.dowoom.viewmodel.comuViewmodel.GuestWriteViewModel

class GuestWriteActivity : BaseActivity<ActivityGuestWriteBinding>("익게 글쓰기 ac", R.layout.activity_guest_write) , View.OnClickListener{

    val viewModel: GuestWriteViewModel by viewModels()

    var subject:String? = null //제목
    var content:String? = null //내용

    override fun onDestroy() {
        super.onDestroy()
        subject = null
        content = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialized()


        //글 작성하기 버튼!

        //insert 후 onSuccessful 이면 true 아니면 false
        viewModel.result.observe(this@GuestWriteActivity, Observer { result ->
            if (result) {
                finish()
            } else {
                Log.d("abcd","GuestWriteActivity - observe - 익명게시판 글쓰기 실패")
            }

        })

    }

    fun initialized() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.tvWritedIn.setOnClickListener(this@GuestWriteActivity)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvWritedIn -> {
                Log.d("abcd","클릭됨")
                subject = binding.etSubject.text.toString()
                content = binding.etTextContent.text.toString()


                //todo: 이미지 업로드할 수 있도록 기능추가 (not now!)
                if (subject.isNullOrEmpty()) {
                    Toast.makeText(this@GuestWriteActivity,"제목을 입력해 주세요.",Toast.LENGTH_SHORT).show()
                }
                if (content.isNullOrEmpty()) {
                    Toast.makeText(this@GuestWriteActivity,"내용을 입력해 주세요.",Toast.LENGTH_SHORT).show()
                }

                viewModel.insertGuestWriteIn(subject!!, content!!)
                Log.d("abcd","GuestWriteActivity - 익명게시판 글쓰기 완료")
            }
        }
    }

}
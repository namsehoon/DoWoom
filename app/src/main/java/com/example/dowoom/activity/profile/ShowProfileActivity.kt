package com.example.dowoom.activity.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.Util.CustomBlockDialog
import com.example.dowoom.Util.GlideApp
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityShowProfileBinding
import com.example.dowoom.viewmodel.ShowProfileViewModel
import com.example.dowoom.viewmodel.loginViewmodel.LoginViewmodel
import java.sql.Ref

class ShowProfileActivity : BaseActivity<ActivityShowProfileBinding>("프로필 액티비티", R.layout.activity_show_profile) {

    val vm: ShowProfileViewModel by viewModels()
    var partnerUid:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        initializeViewModel()

        //차단하기
        binding.tvBlock.setOnClickListener {
           if (!partnerUid.isNullOrEmpty()) {
               vm.blockUser(partnerUid!!)
           }
        }
    }

    fun initialize() {
        binding.vm = vm
        binding.lifecycleOwner = this

        val intent = intent

        partnerUid = intent.getStringExtra("partnerId")
        var age = intent.getIntExtra("partnerAge",0)
        var popularity = intent.getIntExtra("partnerPopularity",0)
        var sorb = intent.getBooleanExtra("partnerSOrB",true)
        var statusMsg = intent.getStringExtra("partnerStateMsg")
        //상대방 nickname
        var nickname = intent.getStringExtra("partnerNickname")
        var profileImg = intent.getStringExtra("profileImg")

        binding.profilePopularity.text = popularity.toString()
        binding.proifileAge.text = age.toString()
        binding.profileStateMsg.text = statusMsg.toString()
        binding.proifileNickname.text = nickname.toString()

        if (profileImg.isNullOrEmpty()) {
            val res = R.drawable.ic_baseline_person_24
            binding.profileImg.setImageDrawable(this.getDrawable(res))
        } else {
            GlideApp.with(this)
                .load(profileImg) // 이미지를 로드
                .override(80,80)
                .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                .error(R.drawable.ic_baseline_person_24) // 불러오다가 에러발생
                .into(binding.profileImg) //이미지를 보여줄 view를 지정
        }

        //상대방이 나 차단함?
        vm.observeBlock(partnerUid!!)

    }

    fun initializeViewModel() {
        lifecycleScope.launchWhenResumed {
            vm.blockCheck.observe(this@ShowProfileActivity, Observer { blocked ->
                if (blocked) {
                    val dialog = CustomBlockDialog(this@ShowProfileActivity)
                    dialog.start("상대방의 프로필을 볼 수 없습니다.")
                    dialog.onOkClickListener(object : CustomBlockDialog.onDialogCustomListener {
                        override fun onClicked() {
                            finish()
                        }

                    })
                }
            })
        }
    }


}
package com.example.dowoom.activity.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.collection.arraySetOf
import androidx.lifecycle.Observer
import com.example.dowoom.R
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.GlideApp
import com.example.dowoom.Util.HandleProfileImage
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityChangeProfileBinding
import com.example.dowoom.databinding.ActivityShowProfileBinding
import com.example.dowoom.model.User
import com.example.dowoom.viewmodel.profile.ChangeProfileViewModel
import com.example.dowoom.viewmodel.profile.ShowProfileViewModel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import java.io.IOException

class ChangeProfileActivity  : BaseActivity<ActivityChangeProfileBinding>("프로필 변경 액티비티", R.layout.activity_change_profile) {

    val vm: ChangeProfileViewModel by viewModels()
    var user:User? = null

    //start result for activity
    val TAKE_IMAGE_CODE = 10001
    //start result for activity
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    //이미지
    var uriList : MutableList<Uri> = mutableListOf<Uri>()
    var progressDialog:CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = vm
        binding.lifecycleOwner = this

        initialized()
        initializedViewmodel()

        //프로필 저장
        binding.tvSave.setOnClickListener {
            val status = binding.etprofileStateMsg.text.toString()
            vm.updateUserProfile(status)
        }

        binding.profileImg.setOnClickListener {
            //권한 획득
            PermissionCheck(this@ChangeProfileActivity).checkPermission()

            //이미지 타입
            val mimeType = arraySetOf<MimeType>(MimeType.JPEG, MimeType.PNG)
            //이미지 선택기 라이브러리
            Matisse.from(this@ChangeProfileActivity)
                .choose(mimeType) //이미지 타입
                .countable(true) //이미지 카운터
                .maxSelectable(1) //이미지 선택 최대 개수
                .thumbnailScale(1f)
                .imageEngine(GlideEngine())
                .showPreview(false)
                .forResult(TAKE_IMAGE_CODE)
        }

        //start for result
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

            })

    }

    fun initialized() {
        vm.getUserInfo()

    }

    fun initializedViewmodel() {
        vm.user.observe(this, Observer { user ->
            binding.user = user
            if (user.profileImg.isNullOrEmpty()) {
                val res = R.drawable.ic_baseline_person_24
                binding.profileImg.setImageDrawable(this.getDrawable(res))
            } else {
                GlideApp.with(this)
                    .load(user.profileImg) // 이미지를 로드
                    .override(150,150)
                    .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                    .error(R.drawable.ic_baseline_person_24) // 불러오다가 에러발생
                    .into(binding.profileImg) //이미지를 보여줄 view를 지정
            }
        })

        vm.completed.observe(this, Observer { completed ->
            if (completed) {
                Toast.makeText(this,"프로필이 업데이트 되었습니다.",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"프로필 업데이트가 실패 했습니다.",Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            //progress 다이어로그
            uriList.clear()
            val context = this@ChangeProfileActivity
            progressDialog = CustomProgressDialog(context)
            progressDialog!!.start()
            uriList = Matisse.obtainResult(data)
            var bitmap: Bitmap? = null

            if (uriList.isNotEmpty()) {
                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,uriList[0]))
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriList[0])
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.profileImg.setImageBitmap(bitmap!!)
                HandleProfileImage(context,bitmap)
                    .let { progressDialog!!.dismiss() }
            }
        }

    }
}
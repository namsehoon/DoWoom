package com.example.dowoom.activity.register

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.dowoom.dataStore.DataStore
import com.example.dowoom.dataStore.DataStoreST
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.activity.main.MainActivity
import com.example.dowoom.databinding.ActivityCheckBinding
import com.example.dowoom.viewmodel.registervm.CheckViewmodel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URI
import android.content.pm.PackageManager
import android.graphics.ImageDecoder

import android.os.Build
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.HandleImage
import java.io.IOException


class CheckActivity : BaseActivity<ActivityCheckBinding>(TAG = "CheckActivity", R.layout.activity_check), View.OnClickListener, AdapterView.OnItemSelectedListener {


    val viewModel : CheckViewmodel by viewModels()

    lateinit var datastore:DataStore
    var nickname:String? = null
    var statusMsg:String? = null
    var spinnerText:String? = null

    lateinit var progressDialog:CustomProgressDialog
    
    var currentUser = FirebaseAuth.getInstance()

    //start result for activity
    val TAKE_IMAGE_CODE = 10001

    private val permissionList = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewmodel에 파라미터(repository)들어갈 경우, factory 만들어줘야함.
        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()
        initializedViewmodel()

        //  갤러리
        binding.ivProfile.setOnClickListener {
            //권한 획득
            checkPermission()

            Log.d("abcd","클릭 됨")
            // 모든 사진에 사용하는 공통 위치, 모든 음악과 오디오 파일에 사용하는 또 다른 공통 위치 등이 있습니다.
            // 앱은 플랫폼의 MediaStore API를 사용하여 이 콘텐츠에 액세스할 수 있습니다."
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }

            //startActivity(intent) 를 실행하기 전 해당 intent를 실행시킬 수 있는지 체크할 필요가 있습니다.
            // 만일 기기에 인텐트를 처리할 수 있는 앱이 존재하지 않으면 비정상 종료되기 때문입니다.
            startActivityForResult(intent, TAKE_IMAGE_CODE)
        }
    }

    fun checkPermission() {
        //현재 버전 6.0 미만이면 종료 --> 6이후 부터 권한 허락
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        //각 권한 허용 여부를 확인
        for (permission in permissionList) {
            val chk = checkCallingOrSelfPermission(permission)
            //거부 상태라면
            if (chk == PackageManager.PERMISSION_DENIED) {
                //사용자에게 권한 허용여부를 확인하는 창을 띄운다.
                requestPermissions(permissionList, 0) //권한 검사 필요한 것들만 남는다.
                break
            }


        }

    }

    private fun initializedViewmodel() {
        with(viewModel) {
            //닉네임 확인 버튼 클릭 시
            requestOkOrNot.observe(this@CheckActivity, Observer {
                if (it) {
                    Log.d("abcd","사용할 수 있습니다.")
                    binding.nextBtn.visibility = View.VISIBLE
                    showToast("사용할 수 있습니다.")
                } else {
                    Log.d("abcd","사용할 수 없습니다.")
                    binding.nextBtn.visibility = View.INVISIBLE
                    showToast("사용할 수 없습니다.")
                }
            })
        }
    }

    fun initialized() {
        //datastore
        datastore = DataStoreST.getInstance(this)

        //리스너
        binding.nextBtn.setOnClickListener(this)
        binding.checkBtn.setOnClickListener(this)
        binding.checkSpinner.onItemSelectedListener = this

        //스피너 어뎁터
        ArrayAdapter.createFromResource(this, R.array.choose_one, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.checkSpinner.adapter = adapter
        }


    }


    //리스너
    override fun onClick(v: View?) {
        statusMsg = binding.etStatusMsg.text.toString()
        spinnerText = binding.checkSpinner.selectedItem.toString()


        //todo 물음표 설명란 만들어야 함.
        when(v) {
            //다음으로 넘어감
            binding.nextBtn ->  {
                //DataStore 저장
                saveData(statusMsg!!,spinnerText!!)
                //회원가입 액티비티로
                startNextActivity(MainActivity::class.java)
            }
        }
    }


    fun saveData( statusMsg:String, spinnerText:String) {

        //백그라운드에서 실행 (Default, io)
        CoroutineScope(Dispatchers.Default).launch {
            datastore.storeData("statusMsg", statusMsg)
            datastore.storeData("spinner", spinnerText)
            datastore.storeData("nickname",viewModel.etNickname.value.toString())

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this,"서포터 또는 수혜자를 선택 해주세요.",Toast.LENGTH_SHORT).show()
        return
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE) {
            when(resultCode) {
                RESULT_OK -> {
                    //progress 다이어로그
                    val context = this@CheckActivity
                    progressDialog = CustomProgressDialog(context)
                    progressDialog.start()

                    //프로필 사진 설정
                    var bitmap:Bitmap? = null
                    val uri:Uri = data?.data!!
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,uri))
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        }
                    } catch (e:IOException) {
                        e.printStackTrace()
                    }
                    binding.ivProfile.setImageBitmap(bitmap!!)
                    HandleImage(context,bitmap)
                        .let { progressDialog.dismiss() }
                }

            }
        }
    }




}



















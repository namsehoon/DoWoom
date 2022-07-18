package com.example.dowoom.activity.register

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
import android.graphics.ImageDecoder

import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.HandleProfileImage
import com.example.dowoom.Util.PermissionCheck
import kotlinx.coroutines.*
import java.io.IOException


class CheckActivity : BaseActivity<ActivityCheckBinding>(TAG = "CheckActivity", R.layout.activity_check), View.OnClickListener, AdapterView.OnItemSelectedListener {


    val viewModel : CheckViewmodel by viewModels()

    lateinit var datastore:DataStore
    var statusMsg:String? = null
    var spinnerText:String? = null

    var progressDialog:CustomProgressDialog? = null
    lateinit var intentFormain :Intent

    //start result for activity
    val TAKE_IMAGE_CODE = 10001

    override fun onDestroy() {
        super.onDestroy()
        progressDialog = null
    }
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
            PermissionCheck(this@CheckActivity).checkPermission()

            Log.d("abcd","클릭 됨")
            // 모든 사진에 사용하는 공통 위치, 모든 음악과 오디오 파일에 사용하는 또 다른 공통 위치 등이 있습니다.
            // 앱은 플랫폼의 MediaStore API를 사용하여 이 콘텐츠에 액세스할 수 있습니다."
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .apply { type = MediaStore.Images.Media.CONTENT_TYPE }

            //startActivity(intent) 를 실행하기 전 해당 intent를 실행시킬 수 있는지 체크할 필요가 있습니다.
            // 만일 기기에 인텐트를 처리할 수 있는 앱이 존재하지 않으면 비정상 종료되기 때문입니다.
            startActivityForResult(intent, TAKE_IMAGE_CODE)
        }
    }


    private fun initializedViewmodel() {
        with(viewModel) {
            //닉네임 확인 버튼 클릭 시
            requestOkOrNot.observe(this@CheckActivity, Observer { result ->
                if (result != null) {
                    if (result) {
                        //false = 사용중인 유저 없음
                        Log.d("abcd","사용할 수 없습니다.")
                        binding.nextBtn.visibility = View.INVISIBLE
                        showToast("닉네임을 사용할 수 없습니다.")
                    } else {
                        //true = 사용중인 유저 있음
                        Log.d("abcd","사용할 수 있습니다.")
                        binding.nextBtn.visibility = View.VISIBLE
                        showToast("닉네임을 사용할 수 있습니다.")
                    }
                } else {
                    Log.d("abcd","버튼을 다시 클릭해주세요.")
                }
            })

            insertComplete.observe(this@CheckActivity, Observer { result ->
                if (result) {
                    Log.d("Abcd","여기까지옴 ? 222")
                    progressDialog?.dismiss()
                    startActivity(intentFormain)
                    finish()
                } else {
                    Log.d("abcd","서버와의 통신이 원할하지 않습니다.")
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
        statusMsg = binding.etStatusMsg.text.toString() ?: " "
        spinnerText = binding.checkSpinner.selectedItem.toString()


        //todo 물음표 설명란 만들어야 함.
        when(v) {
            binding.checkBtn -> {
                lifecycleScope.launchWhenResumed {
                   viewModel.nicknameAvailable()
                }
            }
            //다음으로 넘어감
            binding.nextBtn ->  {
                //DataStore 저장
                progressDialog = CustomProgressDialog(this)
                progressDialog!!.start()
                //task내에 해당 속성이 적용된 activity부터 top activity까지 모두 제거한 뒤 해당 activity를 활성화하여 top이 되도록 함
                intentFormain =  Intent(this,MainActivity::class.java).apply { Intent.FLAG_ACTIVITY_CLEAR_TOP }

                //닉네임 넘겨서 닉네임 없으면 다시 로그인 //todo :뭔 개소리야?
                intentFormain.putExtra("nickname",binding.etNickname.text.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    val sOrB = spinnerText.equals("서포터")
                    Log.d("Abcd",", statusmsg : ${statusMsg} , sorb : ${sOrB} ")
                    viewModel.userInsert(statusMsg!!,sOrB)
                }
            }
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
                    progressDialog!!.start()

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
                    HandleProfileImage(context,bitmap)
                        .let { progressDialog!!.dismiss() }
                }

            }
        }
    }
}



















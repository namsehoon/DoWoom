package com.example.dowoom.activity.register

import android.Manifest
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

import android.os.Build




class CheckActivity : BaseActivity<ActivityCheckBinding>(TAG = "CheckActivity", R.layout.activity_check), View.OnClickListener, AdapterView.OnItemSelectedListener {


    val viewModel : CheckViewmodel by viewModels()

    lateinit var datastore:DataStore
    var nickname:String? = null
    var statusMsg:String? = null
    var spinnerText:String? = null
    
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

        //이미지 선택 버튼 imageView
        binding.ivProfile.setOnClickListener {
            checkPermission()

            Log.d("abcd","클릭 됨")
            // 모든 사진에 사용하는 공통 위치, 모든 음악과 오디오 파일에 사용하는 또 다른 공통 위치 등이 있습니다.
            // 앱은 플랫폼의 MediaStore API를 사용하여 이 콘텐츠에 액세스할 수 있습니다."
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            //startActivity(intent) 를 실행하기 전 해당 intent를 실행시킬 수 있는지 체크할 필요가 있습니다.
            // 만일 기기에 인텐트를 처리할 수 있는 앱이 존재하지 않으면 비정상 종료되기 때문입니다.
            if(intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, TAKE_IMAGE_CODE)
            }

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
                    //프로필 사진 설정
                    val bitmap:Bitmap = data?.extras?.get("data") as Bitmap
                    binding.ivProfile.setImageBitmap(bitmap)
                    handleUpload(bitmap)
                }

            }
        }
    }

    fun handleUpload(bitmap: Bitmap) {
        //압축하기
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)

        //로그인한 유저 uid
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        //사진 업로드 and 위치 기억
        val storage = Firebase.storage.reference
            .child("profileImages")
            .child(uid?.plus(".jpeg")!!)

        storage.putBytes(stream.toByteArray())
            .addOnSuccessListener {
                //내 프로필 참조 위치 저장
                getDownloadUrl(storage)
            }
            .addOnFailureListener { error ->
                Log.d("abcd","프로필사진 업데이트 실패 : ${error.message}")
            }
    }

    fun getDownloadUrl(reference: StorageReference) {
        //downloadUrl : 객체를 다운로드하는 데 사용할 수 있는 URL
        reference.downloadUrl
            .addOnSuccessListener(OnSuccessListener {  uri ->
                Log.d("abcd","프로필사진 uri is : ${uri}")
                setUserProfileurl(uri)
            })
    }

    fun setUserProfileurl(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser

        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        user!!.updateProfile(request)
            .addOnSuccessListener(OnSuccessListener {
                Log.d("abcd","프로필 업데이트 성공")
            })
            .addOnFailureListener { error ->
                Log.d("abcd","프로필 업데이트 실패 : ${error.message}")
            }
    }


}



















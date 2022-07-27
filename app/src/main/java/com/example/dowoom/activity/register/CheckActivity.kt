package com.example.dowoom.activity.register

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
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
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.arraySetOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.HandleProfileImage
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.firebase.Ref
import com.google.firebase.database.MutableData
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_check.*
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*


class CheckActivity : BaseActivity<ActivityCheckBinding>(TAG = "CheckActivity", R.layout.activity_check), View.OnClickListener, AdapterView.OnItemSelectedListener {


    val viewModel : CheckViewmodel by viewModels()
    lateinit var datastore:DataStore
    var statusMsg:String? = null
    var spinnerText:String? = null

    var progressDialog:CustomProgressDialog? = null
    lateinit var intentFormain :Intent

    /** age, birth */
    var yearList = (1950..2023).toList()
    var monthList = (1..12).toList()
    var dateList = (1..31).toList()


    /** 이미지 */

    //start result for activity
    val TAKE_IMAGE_CODE = 10001
    //start result for activity
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    //이미지
    var uriList : MutableList<Uri> = mutableListOf<Uri>()

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

            //이미지 타입
            val mimeType = arraySetOf<MimeType>(MimeType.JPEG, MimeType.PNG)
            //이미지 선택기 라이브러리
            Matisse.from(this@CheckActivity)
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

        if (Ref().auth.displayName != null) {
            val intent = Intent(this,MainActivity::class.java).apply { Intent.FLAG_ACTIVITY_CLEAR_TOP }
            startActivity(intent)
        }

        //리스너
        binding.nextBtn.setOnClickListener(this)
        binding.checkBtn.setOnClickListener(this)
        binding.checkSpinner.onItemSelectedListener = this

        //스피너 어뎁터
        ArrayAdapter.createFromResource(this, R.array.choose_one, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.checkSpinner.adapter = adapter
        }



        val yearStrConvertList = yearList.map { it.toString() }
        val monthStrConvertList = monthList.map { it.toString() }
        val dateStrConvertList = dateList.map { it.toString() }


        binding.npYear.run {
            minValue = 1950
            maxValue = 2023
            wrapSelectorWheel = false
            displayedValues = yearStrConvertList.toTypedArray()
        }

        binding.npMonth.run {
            minValue = 0
            maxValue = monthStrConvertList.size - 1
            // default 값은 true로 false시 picker의 범위가 시작 ~ 끝으로 고정
            wrapSelectorWheel = false
            displayedValues = monthStrConvertList.toTypedArray()
        }

        binding.npDay.run {
            minValue = 0
            maxValue = dateStrConvertList.size - 1
            wrapSelectorWheel = false
            displayedValues = dateStrConvertList.toTypedArray()
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
                val calendar = Calendar.getInstance()
                //나이
                val age = calendar.get(Calendar.YEAR) - binding.npYear.value + 1 //
                //생일
                val birthday: Int = if (binding.npMonth.value > 9) {
                    (binding.npMonth.value + 1) * 1000 + (binding.npDay.value + 1)
                } else {
                    (binding.npMonth.value + 1) * 100 + (binding.npDay.value + 1)
                }

                Log.d("abcd","age : $age, birth : $birthday")

                //task내에 해당 속성이 적용된 activity부터 top activity까지 모두 제거한 뒤 해당 activity를 활성화하여 top이 되도록 함
                intentFormain =  Intent(this,MainActivity::class.java).apply { Intent.FLAG_ACTIVITY_CLEAR_TOP }

                //닉네임 넘겨서 닉네임 없으면 다시 로그인
                intentFormain.putExtra("nickname",binding.etNickname.text.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    val sOrB = spinnerText.equals("서포터")
                    Log.d("Abcd",", statusmsg : ${statusMsg} , sorb : ${sOrB} ")
                    viewModel.userInsert(statusMsg!!,sOrB,age ,birthday)
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
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            //progress 다이어로그
            uriList.clear()
            val context = this@CheckActivity
            progressDialog = CustomProgressDialog(context)
            progressDialog!!.start()
            uriList = Matisse.obtainResult(data)
            var bitmap:Bitmap? = null

            if (uriList.isNotEmpty()) {
                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,uriList[0]))
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriList[0])
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



















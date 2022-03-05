package com.example.dowoom.activity.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dowoom.DataStore.DataStore
import com.example.dowoom.DataStore.DataStoreST
import com.example.dowoom.R
import com.example.dowoom.Repo.nicknameCheckRepo
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityCheckBinding
import com.example.dowoom.viewmodel.registerViewmodel.CheckViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CheckActivity : BaseActivity<ActivityCheckBinding>(TAG = "CheckActivity", R.layout.activity_check), View.OnClickListener, AdapterView.OnItemSelectedListener {


    lateinit var  checkViewmodel: CheckViewmodel
    lateinit var datastore:DataStore
    var nickname:String? = null
    var statusMsg:String? = null
    var spinnerText:String? = null

    var result : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialized()



    }

    fun initialized() {
        //뷰 모델
        checkViewmodel = ViewModelProvider(this).get(CheckViewmodel::class.java)
        //datastore
        datastore = DataStoreST.getInstance(this)

        //리스너
        binding.beforeBtn.setOnClickListener(this)
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
        nickname = binding.etNickname.text.toString()
        statusMsg = binding.etStatusMsg.text.toString()
        spinnerText = binding.checkSpinner.selectedItem.toString()

        //todo 물음표 설명란 만들어야 함.
        when(v) {
            //이전으로 되돌아 감
            binding.beforeBtn -> {
                   //todo 이전 액티비티
            }
            //다음으로 넘어감
            binding.nextBtn ->  {
                //DataStore 저장
                saveData(nickname!!,statusMsg!!,spinnerText!!)
                //회원가입 액티비티로
                startNextActivity(RegisterActivity::class.java)
            }
            binding.checkBtn -> {
                checkViewmodel.getResult(nickname!!).observe(this, {
                    if(it == true) {
                        // 닉네임을 사용 할 수 있으면
                        binding.nextBtn.visibility = View.VISIBLE
                    } else {
                        // 사용 할 수 없으면
                        binding.nextBtn.visibility = View.INVISIBLE
                        Toast.makeText(this@CheckActivity, "이미 등록된 닉네임 입니다.", Toast.LENGTH_SHORT).show()
                    }
                })


            }

        }
    }

    fun saveData(nickname:String, statusMsg:String, spinnerText:String) {

        //백그라운드에서 실행 (Default, io)
        CoroutineScope(Dispatchers.Default).launch {
            datastore.storeData("nickname",nickname)
            datastore.storeData("statusMsg", statusMsg)
            datastore.storeData("spinner", spinnerText)

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this,"서포터 또는 수혜자를 선택 해주세요.",Toast.LENGTH_SHORT).show()
        return
    }
}
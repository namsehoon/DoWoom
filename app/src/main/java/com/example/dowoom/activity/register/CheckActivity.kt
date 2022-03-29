package com.example.dowoom.activity.register

import android.os.Bundle
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
import com.example.dowoom.databinding.ActivityCheckBinding
import com.example.dowoom.viewmodel.registervm.CheckViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckActivity : BaseActivity<ActivityCheckBinding>(TAG = "CheckActivity", R.layout.activity_check), View.OnClickListener, AdapterView.OnItemSelectedListener {


    val viewModel : CheckViewmodel by viewModels()

    lateinit var datastore:DataStore
    var nickname:String? = null
    var statusMsg:String? = null
    var spinnerText:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewmodel에 파라미터(repository)들어갈 경우, factory 만들어줘야함.
        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()
        initializedViewmodel()
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
                startNextActivity(RegisterActivity::class.java)
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

    override fun onDestroy() {
        super.onDestroy()

    }
}
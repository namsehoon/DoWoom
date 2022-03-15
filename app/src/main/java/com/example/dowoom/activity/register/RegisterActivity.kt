package com.example.dowoom.activity.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.dowoom.DataStore.DataStore
import com.example.dowoom.DataStore.DataStoreST
import com.example.dowoom.Model.User
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.R
import com.example.dowoom.activity.login.StartActivity
import com.example.dowoom.databinding.ActivityRegisterBinding
import com.example.dowoom.viewmodel.registervm.RegisterViewmodel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.dowoom.activity.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

//1. 인증문자 보내기
//2. Callbacks 구현 (성공, 실패, )
//3. credential
//4. Sign in (startactivity to main), create user
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(TAG = "RegisterActivity", R.layout.activity_register) {

    private lateinit var auth:FirebaseAuth
    lateinit var datastore: DataStore
    var nickname:String? = null
    var age: Int = 0
    var stateMsg: String? = null
    var sOrB: Boolean? = false
    var authNum:String? = null
    var phoneNum: String? = null


//    private val registerViewmodel by lazy {
//        ViewModelProvider(this).get(RegisterViewmodel::class.java)
//    }

    val viewModel : RegisterViewmodel by viewModels()

    private var resendToken:PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId = ""
    private lateinit var firebase:FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialized()
    }



    fun initialized() {
        //auth
        auth = Firebase.auth
        //viewmodel
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //viwmodel call back 실행
        initViewmodelCallback()
        datastore = DataStoreST.getInstance(this)

        //firebase
        firebase = Firebase.database
    }

    //콜백
    private val callbacks by lazy {
        object  : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //번호인증 혹은 기타 다른인증 끝난 상태
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("abcd","인증코드가 전송되었습니다. 90초 이내에 입력해주세요.")

                //폰 번호 저장
                CoroutineScope(Dispatchers.IO).launch {
                    datastore.storeData("number", viewModel.etPhoneNum.value.toString())
                    authNum = datastore.storeData("authNum", credential.smsCode.toString()).toString()
                }
                //인증코드 입력
                binding.etAuthNumber.setText(credential.smsCode.toString())
                binding.etAuthNumber.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    verifyPhoneNumberWithCode(credential)
                },1000)

            }

            //번호인증 실패
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("abcd","error in 인증실패 : "+e)
                if(e is FirebaseAuthInvalidCredentialsException) {
                    //잘못된 요청
                } else if(e is FirebaseTooManyRequestsException) {
                    Log.d("Abcd","너무 요청이 많습니다. : "+e)
                }
                showToast("인증실패")
            }

            //전화번호는 확인 되었으나 인증코드를 입력해야하는 상황
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                //입력 input 보여주기
                binding.registerLL.visibility = View.VISIBLE
                Log.d("abcd", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Log.d("abacd","인증 시간이 초과 되었습니다. 재전송을 클릭 해주세요."+p0)
            }

        }
    }


    private fun initViewmodelCallback() {
        //with : 수신객체, 수신객체 지정 람다.
        with(viewModel) {
            //인증 요청
            requestAuth.observe(this@RegisterActivity, Observer {
                if (it) {
                    //번호 가져오기
                    requestVerifyByPhoneNumber(
                        "+82" + viewModel.etPhoneNum.value.toString().substring(1) // 1076401370 , 0이 빠짐
                    )
                } else {
                    showToast("전화번호를 입력 해주세요.")
                }
            })

            //다시 인증 요청
            requestResendPhoneAuth.observe(this@RegisterActivity, Observer {
                if (it) {
                    resendVerifyCode("+82"+viewModel.etPhoneNum.value.toString().substring(1),resendToken)
                } else {
                    showToast("전화번호를 입력해주세요.")
                }
            })

            //인증완료 버튼 클릭
            authComplete.observe(this@RegisterActivity, Observer {
                    //인증
                try {
                    val phoneCredential =
                        PhoneAuthProvider.getCredential(
                            storedVerificationId,
                            viewModel.etAuthNum.value.toString()
                        )

                    verifyPhoneNumberWithCode(phoneCredential)
                }catch (e:Exception) {
                    Log.d("abcd","인증완료 버튼 클릭 : "+e.toString())
                }

            })
        }
    }

    //이미 있는 유저라면, 로그인으로 처리
    private fun ifUserExist() {
        phoneNum = binding.etPhoneNumber.text.toString()
        if (phoneNum == binding.etPhoneNumber.text.toString() && authNum == binding.etAuthNumber.text.toString())
        { // 이전에  인증한 번호와 인증번호인 경우
            showToast("인증 성공")
            CoroutineScope(Dispatchers.Main).launch {
                datastore.storeData("number",binding.etPhoneNumber.text.toString())
            }
            startActivity(intent)
            return
        }
    }

    // 사용자 로그인
    private fun verifyPhoneNumberWithCode(phoneAuthCredential: PhoneAuthCredential) {
        //재사용
        CoroutineScope(Dispatchers.IO).launch {
            nickname = datastore.readData("nickname")
            stateMsg = datastore.readData("statusMsg")
            val sb = datastore.readData("spinner")
            sOrB = sb.equals("서포터")
        }

        val intent = Intent(this,MainActivity::class.java)
        //task내에 해당 속성이 적용된 activity부터 top activity까지 모두 제거한 뒤 해당 activity를 활성화하여 top이 되도록 함
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //이미 있는 유저라면,
        ifUserExist()


        //신규
        Firebase.auth.signInWithCredential(phoneAuthCredential)
          .addOnCompleteListener(this) { task ->
              if(task.isSuccessful) {
                  showToast("인증성공")
                  binding.etAuthNumber.isEnabled = true

                  val job = CoroutineScope(Dispatchers.IO).launch {
                      //유저 정보
                      val newUser = task.result?.user
                      datastore.storeData("uid",newUser?.uid!!)
                      datastore.storeData("number", newUser.phoneNumber!!)
                  }
                  job.isCompleted.let {
                      viewModel.userInsert(nickname!!,stateMsg!!,sOrB!!)
                      startActivity(intent)
                  }

                  //edit text auth number input enabled true
              } else {
                  showToast("인증실패")
                  Log.d("abcd", "사용자 로그인 실패 : "+task.exception?.message)
                  binding.etAuthNumber.isEnabled = true
              }

          }
    }

    //인증코드 다시 요청(토큰 필요)
    private fun resendVerifyCode(phoneNumber: String, token:PhoneAuthProvider.ForceResendingToken?) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(90L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        if(token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    //인증코드 요청
    private fun requestVerifyByPhoneNumber(phoneNumber : String) {
        //firebase.auth를 넣어줌
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) //폰번호 입력
            .setTimeout(90L, TimeUnit.SECONDS) //90초
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }


}

package com.example.dowoom.activity.register

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.dowoom.DataStore.DataStore
import com.example.dowoom.DataStore.DataStoreST
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.R
import com.example.dowoom.databinding.ActivityRegisterBinding
import com.example.dowoom.viewmodel.registervm.RegisterViewmodel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.dowoom.activity.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
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


//    private val registerViewmodel by lazy {
//        ViewModelProvider(this).get(RegisterViewmodel::class.java)
//    }

    val viewModel : RegisterViewmodel by viewModels()

    private var resendToken:PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId = ""

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
    }

    //콜백
    private val callbacks by lazy {
        object  : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //번호인증 혹은 기타 다른인증 끝난 상태
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                showToast("인증코드가 전송되었습니다. 90초 이내에 입력해주세요.")

                //폰 번호 저장
                CoroutineScope(Dispatchers.IO).launch {
                    datastore.storeData("number", viewModel.etPhoneNum.value.toString())
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

    //인증실행
    private fun verifyPhoneNumberWithCode(phoneAuthCredential: PhoneAuthCredential) {
        //기존
//        UserInfo.tel = binding.phoneAuthEtPhoneNum.text.toString()
//        if (UserInfo.tel.isNotBlank() && UserInfo.phoneAuthNum.isNotBlank() &&
//            (UserInfo.tel == binding.phoneAuthEtPhoneNum.text.toString() && UserInfo.phoneAuthNum == binding.phoneAuthEtAuthNum.text.toString())
//        ) { // 이전에  인증한 번호와 인증번호인 경우
//            showToast("인증 성공")
//            UserInfo.tel = binding.phoneAuthEtPhoneNum.text.toString()
//            startActivity(Intent(this@PhoneAuthActivity, UserInfoActivity::class.java))
//            return
//        }

        //신규
        Firebase.auth.signInWithCredential(phoneAuthCredential)
          .addOnCompleteListener { task ->
              if(task.isSuccessful) {
                  showToast("인증성공")
                  binding.etAuthNumber.isEnabled = true
                  startNextActivity(MainActivity::class.java)
                  //edit text auth number input enabled true
              } else {
                  showToast("인증실패")
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

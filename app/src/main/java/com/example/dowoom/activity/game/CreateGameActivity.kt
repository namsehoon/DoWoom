package com.example.dowoom.activity.game

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import com.example.dowoom.R
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.HandleImage
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityCreateGameBinding
import com.example.dowoom.generated.callback.OnClickListener
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.example.dowoom.viewmodel.gameViewmodel.GameCreateViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateGameActivity : BaseActivity<ActivityCreateGameBinding>(TAG = "게임생성", R.layout.activity_create_game) {

    val viewModel: GameCreateViewModel by viewModels()
    val currentUser = FirebaseAuth.getInstance().currentUser
    // 사다리게임
    val LADDER_GAME = 1
    // 돌림판
    val CIRCLE_GAME = 2
    // 선착순게임
    val FASTER_GAME = 3

    //start result for activity
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialized()

        //게임 선택 (radio group)
        binding.rgRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                //사다리게임
                R.id.rbLadderGame -> {
                    //1. 상품 개수 가져오기 (최대6개)
                    //2. 총 인원 : 6 - 사진개수 : 나머지는 꽝
                    //3. 그 다음에 꽝이랑 사진 랜덤 섞기
                    //4. 게임 생성(방제목 ,닉네임, )
                }
                //돌림판
                R.id.rbCircleGame -> {

                }
                //선착순 게임
                R.id.rbFasterGame -> {

                }
            }
        }

        //상품 선택
        binding.ivChoosePresent.setOnClickListener {
            //권한 체크
            PermissionCheck(this@CreateGameActivity).checkPermission()

            val mimeType = arrayOf<String>("image/jpeg","image/png")
            val intent = Intent(Intent.ACTION_PICK)
                .apply { type = "image/*"
                    putExtra(Intent.EXTRA_MIME_TYPES,mimeType)
                }

            activityResultLauncher.launch(Intent.createChooser(intent, "앨범 가져오기"))

        }

        //사진 가져오기
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    Log.d("abcd","it.data is : ${result.data}")
                    var selectedImageUri : Uri = result?.data?.data!!

                } else {
                    Toast.makeText(this@CreateGameActivity,"사진을 가져올 수 없습니다.",Toast.LENGTH_SHORT).show()
                }
            })

        //todo 이미지 floating button
        //todo 이미지 floating button
        //todo 이미지 floating button
        //todo 이미지 floating button
    }

    fun initialized() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //라디오

    }

}
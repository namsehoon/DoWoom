package com.example.dowoom.activity.game

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.collection.arraySetOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.HandleGameMultiImages
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.MultiImageAdapter
import com.example.dowoom.databinding.ActivityCreateGameBinding

import com.example.dowoom.viewmodel.gameViewmodel.GameCreateViewModel
import com.google.firebase.auth.FirebaseAuth
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateGameActivity : BaseActivity<ActivityCreateGameBinding>(TAG = "게임생성", R.layout.activity_create_game) {

    val viewModel: GameCreateViewModel by viewModels()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // 사다리게임
    val LADDER_GAME = 1
    // 돌림판
    val CIRCLE_GAME = 2
    // 선착순게임
    val FASTER_GAME = 3
    var whatKindGame = 0

    //이미지 코드
    val CHOOSE_IMAGES = 123;

    //start result for activity
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    //이미지 리스트
    var uriList : MutableList<Uri> = mutableListOf<Uri>()
    //이미지 recyclerview adapter
    private lateinit var adapter: MultiImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialized()

        //게임 선택 (radio group)
        binding.rgRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                //사다리게임
                R.id.rbLadderGame -> {
                    whatKindGame = LADDER_GAME
                }
                //돌림판
                R.id.rbCircleGame -> {
                    whatKindGame = CIRCLE_GAME
                }
                //선착순 게임
                R.id.rbFasterGame -> {
                    whatKindGame = FASTER_GAME
                }
                else -> {
                    whatKindGame = LADDER_GAME
                }
            }
        }

        //상품 선택
        binding.ivChoosePresent.setOnClickListener {
            //권한 체크
            PermissionCheck(this@CreateGameActivity).checkPermission()
            //이미지 타입
            val mimeType = arraySetOf<MimeType>(MimeType.JPEG, MimeType.PNG)
            //이미지 선택기 라이브러리
            Matisse.from(this@CreateGameActivity)
                .choose(mimeType) //이미지 타입
                .countable(true) //이미지 카운터
                .maxSelectable(6) //이미지 선택 최대 개수
                .thumbnailScale(1f)
                .imageEngine(GlideEngine())
                .showPreview(false)
                .forResult(CHOOSE_IMAGES)

        }

        //start for result
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

            })

        //게임 생성
        binding.tvCreateGameRoom.setOnClickListener {
            val progress = CustomProgressDialog(this@CreateGameActivity)
            progress.start()
            CoroutineScope(Dispatchers.IO).launch {
                //todo : 사진이 한장도 없으면 안됨
                createGame()
            }
            progress.dismiss()
        }
    }

    private suspend fun createGame() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("abcd","createGame() 게임 생성")
            val handleImage = HandleGameMultiImages(uriList) // storage에 상품 올리기, gameuid 만들기
            val gameUid = handleImage.handleUpload()

            //게임 종류 별 게임 생성
            if (whatKindGame == 0 || whatKindGame == 1) { // 사다리 게임
                createLadderGame(gameUid)

            } else if (whatKindGame == 2) { // 돌림판
                //todo
            } else if(whatKindGame == 3) {
                //todo
            }
        }

    }

    //사다리 게임 생성
    private suspend fun createLadderGame(gameUid : String) {
        //사다리 게임
        CoroutineScope(Dispatchers.IO).launch {
            val ladder = LadderGame() // 사다리게임 인스턴스 생성
            val new = ladder.generateLadder(6,6) // 6 x 6
            val resultList = ladder.randomPresent(new) // 결과 리스트
            //방 제목, 닉네임, gameuid, 방인원, 남은인원, 게임
            Log.d("abcd","여기는 언제 올까? : game id : ${gameUid}")
            viewModel.createGame(whatKindGame, resultList, gameUid)
        }

    }


    fun initialized() {
        binding.vm = viewModel
        binding.lifecycleOwner = this


        viewModel.getOnEndLive().observe(this, Observer { onEnd ->
            if (onEnd != null && onEnd) {
                finish()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGES && resultCode == RESULT_OK) {
                    uriList = Matisse.obtainResult(data)

                    if (uriList.isEmpty()) { //만약 사진이 한장도 없다면,,
                        Toast.makeText(this@CreateGameActivity,"사진을 선택 해주세요.",Toast.LENGTH_SHORT).show()
                    } else {
                        //adapter 설정
                        adapter = MultiImageAdapter(uriList, this@CreateGameActivity, deleteClicked =  { uri, position ->
                            uriList.remove(uri)
                            adapter.notifyItemRemoved(position)
                        })
                        binding.rvChoiceImage.adapter = adapter
                        binding.rvChoiceImage.layoutManager = LinearLayoutManager(this@CreateGameActivity, LinearLayoutManager.HORIZONTAL,true)

                    }
        }
    }
}
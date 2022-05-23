package com.example.dowoom.activity.game

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.MultiImageAdapter
import com.example.dowoom.databinding.ActivityCreateGameBinding
import com.example.dowoom.model.LadderGameModel
import com.example.dowoom.model.Present
import com.example.dowoom.viewmodel.gameViewmodel.GameCreateViewModel
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

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

    //start result for activity
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    //이미지 리스트
    var uriList : ArrayList<Uri> = ArrayList()
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
            }
        }

        //상품 선택
        binding.ivChoosePresent.setOnClickListener {
            //권한 체크
            PermissionCheck(this@CreateGameActivity).checkPermission()

            val mimeType = arrayOf<String>("image/jpeg","image/png")
            val intent = Intent(Intent.ACTION_PICK)
                .apply { type = "image/*"
                    //사진 여러 장
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    //사진 타입
                    putExtra(Intent.EXTRA_MIME_TYPES,mimeType)
                }

            activityResultLauncher.launch(Intent.createChooser(intent, "앨범 가져오기"))

        }

        //start for result
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                   if (result?.data?.clipData == null) { // 이미지가 '하나' 인 경우
                       Log.d("abcd","이미지 한 개 : ${result?.data?.data}")
                       val selectedSingleImageUri = result?.data?.data!!
                        uriList.add(selectedSingleImageUri)

                       //adapter 설정
                       adapter = MultiImageAdapter(uriList, this@CreateGameActivity, deleteClicked =  { uri, position ->
                           adapter.notifyItemRemoved(position)
                       })
                       binding.rvChoiceImage.adapter = adapter
                       binding.rvChoiceImage.layoutManager = LinearLayoutManager(this@CreateGameActivity, LinearLayoutManager.HORIZONTAL,true)
                   } else { // '여러 장' 인 경우
                       Log.d("abcd","이미지 개수 : ${result.data?.clipData?.itemCount}")
                       val clipData = result.data?.clipData

                       if (clipData?.itemCount!! > 6) { //이미지가 6장 이상
                           Toast.makeText(this@CreateGameActivity, "6장이 최대 입니다.",Toast.LENGTH_SHORT).show()

                       } else {
                           for (i in 0..clipData.itemCount) {
                               val selectedMultiImageUri = clipData.getItemAt(i).uri

                               try {//리스트에 사진 담기
                                   uriList.add(selectedMultiImageUri)

                               } catch (e: Exception) {
                                   e.printStackTrace()
                               }
                           }
                           adapter = MultiImageAdapter(uriList, this@CreateGameActivity, deleteClicked = { uri,position ->
                               adapter.notifyItemRemoved(position)
                           })
                           binding.rvChoiceImage.adapter = adapter
                           binding.rvChoiceImage.layoutManager = LinearLayoutManager(this@CreateGameActivity, LinearLayoutManager.HORIZONTAL,true)


                       }
                   }

                } else {
                    Toast.makeText(this@CreateGameActivity,"사진을 가져올 수 없습니다.",Toast.LENGTH_SHORT).show()
                }
            })

        //게임 생성
        binding.tvCreateGameRoom.setOnClickListener {
            createGame()
        }
    }

    private fun createGame() {
        uriList

        if (whatKindGame == 1) { // 사다리 게임
            createLadderGame()

        } else if (whatKindGame == 2) { // 돌림판

        } else if(whatKindGame == 3) // 선착순 게임

    }

    private fun createLadderGame() {
        //사다리 게임
        val ladder = LadderGame()
        val new = ladder.generateLadder(6,6)
        val result = ladder.randomPresent(new)
        val ladderModel = LadderGameModel(result[0], result[1], result[2], result[3], result[4], result[5])
        val presentModel = Present(uriList[0], uriList[1], uriList[2],uriList[3],uriList[4],uriList[5])
        Log.d("abcd","result is : $result")
    }


    fun initialized() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

    }

}
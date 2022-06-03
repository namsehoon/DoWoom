package com.example.dowoom.activity.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.Util.CustomGameDialog
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityCreateGameBinding
import com.example.dowoom.databinding.ActivityPlayGameBinding
import com.example.dowoom.model.GameResultModel
import com.example.dowoom.viewmodel.gameViewmodel.GameCreateViewModel
import com.example.dowoom.viewmodel.gameViewmodel.GamePlayViewModel

class PlayGameActivity : BaseActivity<ActivityPlayGameBinding>(TAG = "게임 플레이", R.layout.activity_play_game), View.OnClickListener {

    val viewModel: GamePlayViewModel by viewModels()

    var gameUid:String? = null
    var leftCount:Int? = null
    var active:Boolean? = null
    var creator:String? = null
    var whatKindGame:Int? = null

    var gameResult:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()
        observeData()
    }

    private fun observeData() {
        viewModel.img.observe(this@PlayGameActivity, Observer { result ->
            Log.d("abcd","선물 img is : ${result}")
            gameResult = result
            //결과 확인버튼 보이게
            binding.tvShowResult.visibility = View.VISIBLE
            binding.tvShowResult.isClickable = true
        })

        viewModel.getLeftCount(gameUid!!).observe(this@PlayGameActivity, Observer { leftCount ->
            binding.llChoice.removeAllViews()
            createBtn(leftCount)
        })
    }

    fun initialized() {
        //intent 가져오기
        val intent = intent

        gameUid = intent.getStringExtra("gameId")
        leftCount= intent.getIntExtra("leftCount", 0)
        active= intent.getBooleanExtra("active", false)
        creator= intent.getStringExtra("creator")
        whatKindGame= intent.getIntExtra("whatKindGame", 0)


        binding.tvCreator.text = creator ?: "남세훈"

        //todo : 게임 종류에 따라 다름 액티비티로 가야 함


        //결과 확인 버튼
        binding.tvShowResult.setOnClickListener(this@PlayGameActivity)

    }


    //선택버튼 클릭 시,
    override fun onClick(v: View?) {
        lifecycleScope.launchWhenResumed {
            Log.d("abcd","getGameResult is clicked : gameUid : ${gameUid}")
            when(v?.id) { //todo : 선택을 하면 다른 선택지들은 선택할 수 없고 유저 게임 카운터 상승
                0 -> {
                    viewModel.getGameResult(gameUid!!, "one")
                    setClickableFalse()
                }
                1 -> {
                    viewModel.getGameResult(gameUid!!, "two")
                    setClickableFalse()
                }
                2 -> {
                    viewModel.getGameResult(gameUid!!, "three")
                    setClickableFalse()
                }
                3 -> {
                    viewModel.getGameResult(gameUid!!, "four")
                    setClickableFalse()
                }
                4 -> {
                    viewModel.getGameResult(gameUid!!, "five")
                    setClickableFalse()
                }
                5 -> {
                    viewModel.getGameResult(gameUid!!, "six")
                    setClickableFalse()
                }
                R.id.tvShowResult -> {
                    val alertDialog = CustomGameDialog(this@PlayGameActivity,gameResult!!,gameUid!!)
                    alertDialog.start()
                    //게임 fragment로 나가짐
                    alertDialog.onOkClickListener(object : CustomGameDialog.onDialogCustomListener{
                        override fun onClicked() {
                            this@PlayGameActivity.finish()
                        }

                    })
                }
            }
        }
    }
//1.버튼 만들고,
    fun createBtn(leftCount : Int) {
        Log.d("abcd","leftCount is ${leftCount}")
        for (index in 0 until leftCount) {
            val button = TextView(this@PlayGameActivity).apply {
               layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT,1f)
                gravity = Gravity.CENTER
                id = index
                text = "선택"
                Log.d("abcd","leftCount is ${index}")
            }
            button.setOnClickListener(this@PlayGameActivity)

            binding.llChoice.addView(button)
        }
    }

    fun setClickableFalse() {
        binding.llChoice.removeAllViews() //모든 서브뷰 제거

        Toast.makeText(this@PlayGameActivity, "결과 확인 버튼을 클릭 해주세요.", Toast.LENGTH_SHORT).show()
    }
}
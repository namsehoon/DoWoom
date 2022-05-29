package com.example.dowoom.activity.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityCreateGameBinding
import com.example.dowoom.databinding.ActivityPlayGameBinding
import com.example.dowoom.viewmodel.gameViewmodel.GameCreateViewModel
import com.example.dowoom.viewmodel.gameViewmodel.GamePlayViewModel

class PlayGameActivity : BaseActivity<ActivityPlayGameBinding>(TAG = "게임 플레이", R.layout.activity_play_game), View.OnClickListener {

    val viewModel: GamePlayViewModel by viewModels()

    var gameUid:String? = null
    var leftCount:Int? = null
    var active:Boolean? = null
    var creator:String? = null
    var whatKindGame:Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()
        observeData()
    }

    private fun observeData() {
        viewModel.img.observe(this, Observer { result ->
            Log.d("abcd","선물 img is : ${result}")
            //todo : 결과
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

        binding.tvOne.setOnClickListener(this@PlayGameActivity)
        binding.tvTwo.setOnClickListener(this@PlayGameActivity)
        binding.tvThree.setOnClickListener(this@PlayGameActivity)
        binding.tvFour.setOnClickListener(this@PlayGameActivity)
        binding.tvFive.setOnClickListener(this@PlayGameActivity)
        binding.tvSix.setOnClickListener(this@PlayGameActivity)

        //gameid에 해당하는 사은 품 가져오기?
    }

    override fun onClick(v: View?) {
        lifecycleScope.launchWhenResumed {
            Log.d("abcd","getGameResult is clicked : gameUid : ${gameUid}")
            when(v?.id) {
                R.id.tvOne -> {
                    viewModel.getGameResult(gameUid!!, "one")
                }
                R.id.tvTwo -> {
                    viewModel.getGameResult(gameUid!!, "two")
                }
                R.id.tvThree -> {
                    viewModel.getGameResult(gameUid!!, "three")
                }
                R.id.tvFour -> {
                    viewModel.getGameResult(gameUid!!, "four")
                }
                R.id.tvFive -> {
                    viewModel.getGameResult(gameUid!!, "five")
                }
                R.id.tvSix -> {
                    viewModel.getGameResult(gameUid!!, "six")
                }
            }
        }
    }
}
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
import com.example.dowoom.model.GameModel
import com.example.dowoom.model.GameResultModel
import com.example.dowoom.viewmodel.gameViewmodel.GameCreateViewModel
import com.example.dowoom.viewmodel.gameViewmodel.GamePlayViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayGameActivity : BaseActivity<ActivityPlayGameBinding>(TAG = "게임 플레이", R.layout.activity_play_game), View.OnClickListener {

    val viewModel: GamePlayViewModel by viewModels()

    var gameUid:String? = null
    var leftCount:Int? = null
    var active:Boolean? = null
    var creator:String? = null
    var whatKindGame:Int? = null

    //게임 결과 찾기
    var gameResult:String? = null

    val RESULT_ONE = "one"
    val RESULT_TWO = "two"
    val RESULT_THREE = "three"
    val RESULT_FOUR = "four"
    val RESULT_FIVE = "five"
    val RESULT_SIX = "six"

    //사용된 결과 삭제
    var result:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()
        observeData()
    }

    private fun observeData() {
        viewModel.img.observe(this@PlayGameActivity, Observer { results ->
            Log.d("abcd","선물 img is : ${results}")
            gameResult = results
            //결과 확인버튼 보이게
            binding.tvShowResult.visibility = View.VISIBLE
            binding.tvShowResult.isClickable = true
        })

        viewModel.getLeftCount(gameUid!!).observe(this@PlayGameActivity, Observer { game ->
            binding.llChoice.removeAllViews()
            createBtn(game!!)
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
        binding.tvShowResult.setOnClickListener {
            val alertDialog = CustomGameDialog(this@PlayGameActivity,gameResult!!,gameUid!!)
            alertDialog.start()
            //게임 fragment로 나가짐
            alertDialog.onOkClickListener(object : CustomGameDialog.onDialogCustomListener{
                override fun onClicked() {
                    Log.d("abcd","삭제할 result is : ${result}")
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteResult(gameUid!!,result!!)
                    } //
                    this@PlayGameActivity.finish()
                }

            })
        }

    }


    //선택버튼 클릭 시,
    override fun onClick(v: View?) {
        lifecycleScope.launchWhenResumed {
            Log.d("abcd","getGameResult is clicked : gameUid : ${gameUid}")
            when(v?.id) {
                0 -> {
                    viewModel.getGameResult(gameUid!!, RESULT_ONE)
                    Log.d("abcd","선택됨 0번째 인덱스, one")
                    result = RESULT_ONE
                    setClickableFalse()
                }
                1 -> {
                    viewModel.getGameResult(gameUid!!, RESULT_TWO)
                    Log.d("abcd","선택됨 1번째 인덱스, two")
                    result = RESULT_TWO
                    setClickableFalse()
                }
                2 -> {
                    viewModel.getGameResult(gameUid!!, RESULT_THREE)
                    Log.d("abcd","선택됨 2번째 인덱스, three")
                    result = RESULT_THREE
                    setClickableFalse()
                }
                3 -> {
                    viewModel.getGameResult(gameUid!!, RESULT_FOUR)
                    Log.d("abcd","선택됨 3번째 인덱스, four")
                    result = RESULT_FOUR
                    setClickableFalse()
                }
                4 -> {
                    viewModel.getGameResult(gameUid!!, RESULT_FIVE)
                    Log.d("abcd","선택됨 4번째 인덱스, five")
                    result = RESULT_FIVE
                    setClickableFalse()
                }
                5 -> {
                    viewModel.getGameResult(gameUid!!, RESULT_SIX)
                    Log.d("abcd","선택됨 5번째 인덱스, six")
                    result = RESULT_SIX
                    setClickableFalse()
                }
                else -> {
                    Log.d("abcd","else 선택됨 : ${v?.id}")
                    setClickableFalse()
                }


            }
        }
    }
    /** 버튼 처리 */
    private fun btn(arra:Array<Int?>, index:Int ) {
        val button = TextView(this@PlayGameActivity).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT,1f)
            gravity = Gravity.CENTER
            Log.d("abcd","index  111 1 is : ${index}")
            if (arra[index] == null) {
                id = index+7
            } else {
                id = arra[index]!!
            }
            text = "선택"
            Log.d("abcd","index 2222 is ${index}")
        }
        button.setOnClickListener(this@PlayGameActivity)
        if (arra[index] == null) {
            button.isEnabled = false
        }
        binding.llChoice.addView(button)
    }

    /** 버튼 생성 */
    private fun createBtn(game:GameModel) {
        val arra = getGameArray(game)

        for (index in 0..5) {
            btn(arra,index)
        }
    }

    /** 게임 선택 박스 배열 */
    private fun getGameArray(game:GameModel) : Array<Int?> {
        val arra = arrayOfNulls<Int>(6)
        if (game.gameResult?.one != null) {
            arra.set(0, game.gameResult?.one!!)
            Log.d("abcd","arra 0 is : ${arra[0]}")
        }
        if(game.gameResult?.two != null) {
            arra.set(1, game.gameResult?.two!!)
            Log.d("abcd","arra 1 is : ${arra[1]}")
        }
        if (game.gameResult?.three != null) {
            arra.set(2, game.gameResult?.three!!)
            Log.d("abcd","arra 2 is : ${arra[2]}")
        }
        if (game.gameResult?.four != null) {
            arra.set(3, game.gameResult?.four!!)
            Log.d("abcd","arra 3 is : ${arra[3]}")
        }
        if (game.gameResult?.five != null) {
            arra.set(4, game.gameResult?.five!!)
            Log.d("abcd","arra 4 is : ${arra[4]}")
        }
        if (game.gameResult?.six != null) {
            arra.set(5, game.gameResult?.six!!)
            Log.d("abcd","arra 5 is : ${arra[5]}")
        }

        Log.d("abcd","arra is : ${arra.toString()}")
        return arra
    }
    /** 버튼 처리 */
    fun setClickableFalse() {
        binding.llChoice.removeAllViews() //모든 서브뷰 제거

        Toast.makeText(this@PlayGameActivity, "결과 확인 버튼을 클릭 해주세요.", Toast.LENGTH_SHORT).show()
    }
}
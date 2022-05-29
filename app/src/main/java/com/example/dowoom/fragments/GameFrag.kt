package com.example.dowoom.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.viewmodel.mainViewmodel.GameViewModel
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.activity.game.CreateGameActivity
import com.example.dowoom.activity.game.LadderGame
import com.example.dowoom.activity.game.PlayGameActivity
import com.example.dowoom.adapter.ChatRoomAdapter
import com.example.dowoom.adapter.GameAdapter
import com.example.dowoom.databinding.GameFragmentBinding
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.GameModel
import com.example.dowoom.viewmodel.gameViewmodel.GamePlayViewModel
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//게임 프레그먼트
class GameFrag : BaseFragment<GameFragmentBinding>("GameFrag", R.layout.game_fragment) {



    companion object {
        fun newInstance() = GameFrag()
    }

    val viewModel by viewModels<GameViewModel>()
    //adapter
    private lateinit var adapter: GameAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //게임 생성
        binding.tvCreateGame.setOnClickListener {
            val intent = Intent(context, CreateGameActivity::class.java)
            context?.startActivity(intent)
        }

        //어뎁터
        adapter = GameAdapter(requireActivity(),
            //게임방으로 들어가기
            goIntoGameRoom = { game ->
                val alertDialog = CustomAlertDialog(requireActivity())

                val intent = Intent(context, PlayGameActivity::class.java)

                intent.putExtra("gameId",game.gameUid)
                Log.d("abcd","gameId is : ${game.gameUid}")
                //현재 이용한 사용자 수
                intent.putExtra("leftCount",game.leftCount)
                //방 활성화
                intent.putExtra("active",game.active)
                //방장 닉네임
                intent.putExtra("creator",game.nickname)
                //게임 종류
                intent.putExtra("whatKindGame",game.whatKindGame)

                alertDialog.start("게임방에 입장 하시겠습니까?")
                alertDialog.onOkClickListener(object :CustomAlertDialog.onDialogCustomListener {
                    override fun onClicked() {
                       CoroutineScope(Dispatchers.Main).launch {
                           delay(500)
                       }
                        context?.startActivity(intent)
                    }

                })

            }
        )

        binding.rvGameList.layoutManager = LinearLayoutManager(this.context)
        binding.rvGameList.adapter = adapter

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //바인딩
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()


        //옵저버 (back key 에러를 보완하기 위해서 여기서 getlifecyclerowner로 관찰)
        observerData()

    }

    private fun observerData() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.observeLadderGame().observe(viewLifecycleOwner, Observer { ladders ->
                Log.d("abcd","ladders s s ss : ${ladders}")
                adapter.setGameRoom(ladders)
            })

        }
    }


}
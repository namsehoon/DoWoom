package com.example.dowoom.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.dowoom.viewmodel.mainViewmodel.GameViewModel
import com.example.dowoom.R
import com.example.dowoom.activity.game.CreateGameActivity
import com.example.dowoom.activity.game.LadderGame
import com.example.dowoom.databinding.GameFragmentBinding
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//게임 프레그먼트
class GameFrag : BaseFragment<GameFragmentBinding>("GameFrag", R.layout.game_fragment) {


    companion object {
        fun newInstance() = GameFrag()
    }

    val viewModel by viewModels<GameViewModel>()



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //게임 생성
        binding.tvCreateGame.setOnClickListener {
            val intent = Intent(context, CreateGameActivity::class.java)
            context?.startActivity(intent)
        }

        initialized()

    }

    private fun initialized() {
        viewModel.gameModel.observe(viewLifecycleOwner, Observer { ladders ->

        })
    }


}
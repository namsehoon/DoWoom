package com.example.dowoom.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import com.example.dowoom.viewmodel.mainViewmodel.GameViewModel
import com.example.dowoom.R
import com.example.dowoom.databinding.GameFragmentBinding

class GameFrag : BaseFragment<GameFragmentBinding>("GameFrag", R.layout.game_fragment) {


    companion object {
        fun newInstance() = GameFrag()
    }

    private lateinit var viewModel: GameViewModel



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        // TODO: Use the ViewModel
    }



}
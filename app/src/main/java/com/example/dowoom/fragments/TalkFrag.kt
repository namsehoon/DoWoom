package com.example.dowoom.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import com.example.dowoom.R
import com.example.dowoom.viewmodel.mainViewmodel.TalkViewModel
import com.example.dowoom.databinding.TalkFragmentBinding

class TalkFrag : BaseFragment<TalkFragmentBinding>("TalkFrag", R.layout.talk_fragment) {


    companion object {
        fun newInstance() = TalkFrag()
    }

    private lateinit var viewModel: TalkViewModel



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TalkViewModel::class.java)
        // TODO: Use the ViewModel
    }



}
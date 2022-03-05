package com.example.dowoom.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuFragmentBinding

class ComuFrag : BaseFragment<ComuFragmentBinding>(TAG = "ComeFrag", R.layout.comu_fragment) {


    companion object {
        fun newInstance() = ComuFrag()
    }

    private lateinit var viewModel: ComuViewModel



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComuViewModel::class.java)
        // TODO: Use the ViewModel
    }



}
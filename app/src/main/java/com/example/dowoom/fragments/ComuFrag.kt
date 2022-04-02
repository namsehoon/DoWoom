package com.example.dowoom.fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuFragmentBinding
import kotlinx.coroutines.launch

class ComuFrag : BaseFragment<ComuFragmentBinding>(TAG = "ComeFrag", R.layout.comu_fragment) {


    companion object {
        fun newInstance() = ComuFrag()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }





}
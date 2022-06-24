package com.example.dowoom.fragments.childFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuHumorFragmentBinding
import com.example.dowoom.databinding.GameFragmentBinding
import com.example.dowoom.fragments.BaseFragment
import com.example.dowoom.viewmodel.comuChildViewModel.ComuHumorViewModel

class ComuHumor : BaseFragment<ComuHumorFragmentBinding>("유머게시판", R.layout.comu_humor_fragment) {

    companion object {
        fun newInstance() = ComuHumor()
    }

    private lateinit var viewModel: ComuHumorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.comu_humor_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComuHumorViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
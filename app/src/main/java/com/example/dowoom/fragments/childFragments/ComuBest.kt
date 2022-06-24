package com.example.dowoom.fragments.childFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuHumorFragmentBinding
import com.example.dowoom.fragments.BaseFragment
import com.example.dowoom.viewmodel.comuChildViewModel.ComuBestViewModel

class ComuBest : BaseFragment<ComuHumorFragmentBinding>("베스트", R.layout.comu_best_fragment){

    companion object {
        fun newInstance() = ComuBest()
    }

    private lateinit var viewModel: ComuBestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.comu_best_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComuBestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
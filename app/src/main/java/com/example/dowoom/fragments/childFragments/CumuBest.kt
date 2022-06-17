package com.example.dowoom.fragments.childFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dowoom.R
import com.example.dowoom.viewmodel.comuChildViewModel.CumuBestViewModel

class CumuBest : Fragment() {

    companion object {
        fun newInstance() = CumuBest()
    }

    private lateinit var viewModel: CumuBestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cumu_best_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CumuBestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
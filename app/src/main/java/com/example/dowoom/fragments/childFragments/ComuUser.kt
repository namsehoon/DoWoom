package com.example.dowoom.fragments.childFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dowoom.R
import com.example.dowoom.viewmodel.comuChildViewModel.ComuUserViewModel

class ComuUser : Fragment() {

    companion object {
        fun newInstance() = ComuUser()
    }

    private lateinit var viewModel: ComuUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.comu_user_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComuUserViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
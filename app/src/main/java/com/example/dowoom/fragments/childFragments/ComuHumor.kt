package com.example.dowoom.fragments.childFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuHumorFragmentBinding
import com.example.dowoom.databinding.GameFragmentBinding
import com.example.dowoom.factory.ComuViewModelFactory
import com.example.dowoom.fragments.BaseFragment
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.comuChildViewModel.ComuHumorViewModel
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ComuHumor : BaseFragment<ComuHumorFragmentBinding>("유머게시판", R.layout.comu_humor_fragment) {

    companion object {
        fun newInstance() = ComuHumor()
    }

    var comuModel:ComuModel? = null
    var position:Int? = null

    private val repository = GezipRepo()
    @ExperimentalCoroutinesApi
    private val viewModel: ComuViewModel by viewModels { ComuViewModelFactory(repository) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()

        observerData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun observerData() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.comuList.observe(viewLifecycleOwner, Observer { it ->
                Log.d("abcd","child of child it is : ${it}")
            })

        }
    }

}
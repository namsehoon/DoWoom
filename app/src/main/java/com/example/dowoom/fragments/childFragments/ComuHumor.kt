package com.example.dowoom.fragments.childFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.adapter.ComuAdapter
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

    var comuModelFromParent:ComuModel? = null
    var position:Int? = null
    var contentTextFromParent:String? = null

    private val repository = GezipRepo()
    @ExperimentalCoroutinesApi
    private val viewModel: ComuViewModel by viewModels { ComuViewModelFactory(repository) }
    //adapter
    private lateinit var adapter: ComuAdapter

    val HUMOR = "유머게시판"

    //관찰
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()

        //todo : 1. progress 고쳐야 함
        //todo : 1. progress 고쳐야 함
        //todo : 1. progress 고쳐야 함

        //todo : 2. 유머게시판 데이터 가져와
        //todo : 2. 유머게시판 데이터 가져와
        //todo : 2. 유머게시판 데이터 가져와

        //todo : 3. 댓글 만들어
        //todo : 3. 댓글 만들어
        //todo : 3. 댓글 만들어

        observerData()
    }

    //버튼 리스너, recyclerview 어뎁터
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ComuAdapter(requireActivity(), contentClicked = { comuModel, position ->

            Log.d("Abcd","${HUMOR} 클릭됨")

            binding.tvTitle.text = comuModel.title.toString()
            binding.tvCreator.text = comuModel.creator.toString()

            //스크롤 맨위로
            binding.nested.pageScroll(View.FOCUS_UP)
            binding.nested.pageScroll(ScrollView.FOCUS_UP)

        })


        // 게시판 이름
        binding.tvKindOf.text = HUMOR
        binding.tvAnother.text = HUMOR

        binding.tvTitle.text = comuModelFromParent?.title.toString() //제목
        binding.tvCreator.text = comuModelFromParent?.creator.toString() //글쓴이

        //layout
        binding.rvComuList.layoutManager = LinearLayoutManager(context)
        binding.rvComuList.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        comuModelFromParent = null

    }

    @ExperimentalCoroutinesApi
    private fun observerData() {
       viewLifecycleOwner.lifecycleScope.launchWhenCreated {
           viewModel.getHumors()
       }

        viewModel.comuList.observe(viewLifecycleOwner, Observer { it ->
            Log.d("abcd","child - 유머게시판 : ${it}")
            adapter.setContents(it)
        })

    }

}
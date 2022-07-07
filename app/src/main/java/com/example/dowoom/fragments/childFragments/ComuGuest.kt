package com.example.dowoom.fragments.childFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.adapter.ComuAdapter
import com.example.dowoom.databinding.ComuGuestFragmentBinding
import com.example.dowoom.databinding.ComuHumorFragmentBinding
import com.example.dowoom.factory.ComuViewModelFactory
import com.example.dowoom.fragments.BaseFragment
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.comuChildViewModel.ComuGuestViewModel
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel
import com.example.dowoom.viewmodel.registervm.LoadingViewmodel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ComuGuest :  BaseFragment<ComuGuestFragmentBinding>("유머게시판", R.layout.comu_guest_fragment)  {

    companion object {
        fun newInstance() = ComuGuest()
    }

    var comuModelFromParent: ComuModel? = null
    var position:Int? = null
    var contentTextFromParent:String? = null

    val GUEST = "익명게시판"

    private val repository = GezipRepo()
    @ExperimentalCoroutinesApi
    private val viewModel: ComuViewModel by viewModels { ComuViewModelFactory(repository) }
    //adapter
    private lateinit var adapter: ComuAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observerData()
    }

    //버튼 리스너, recyclerview 어뎁터
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo adapter 다른거 써야함
        //todo adapter 다른거 써야함
        //todo adapter 다른거 써야함
        //todo adapter 다른거 써야함
        adapter = ComuAdapter(requireActivity(), contentClicked = { comuModel, position ->

            Log.d("Abcd","child에서 ComuAdapter 클릭됨")

            //자식 프로그먼트에서 컨텐츠 클릭할 때 (바뀌는 것)
            binding.tvTitle.text = comuModel.title.toString() // 제목
            binding.tvCreator.text = comuModel.creator.toString() // 글쓴이
            binding.tvContent.text = comuModel.contentText.toString() //컨텐츠 내용

            //클릭 시, 스크롤 맨 위로
            binding.nested.pageScroll(View.FOCUS_UP)
            binding.nested.pageScroll(ScrollView.FOCUS_UP)
        })

        // 안 바뀌는 것
        binding.tvKindOf.text = GUEST // 상위
        binding.tvAnother.text = GUEST // 하위
        binding.tvContent.text = contentTextFromParent.toString()//컨텐츠 내용
        binding.tvTitle.text = comuModelFromParent?.title.toString() // 제목
        binding.tvCreator.text = comuModelFromParent?.creator.toString() //글쓴이

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
            viewModel.getGuest()
        }

        viewModel.guestList.observe(viewLifecycleOwner, Observer {
            Log.d("abcd","child - 익명게시판 : ${it}")
            adapter.setContents(it)
        })



    }

}
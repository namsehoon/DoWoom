package com.example.dowoom.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.Adapter.onlineAdapter
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel
import com.example.dowoom.R
import com.example.dowoom.databinding.HomeFragmentBinding

class HomeFrag : BaseFragment<HomeFragmentBinding>(TAG = "HomeFrag", R.layout.home_fragment) {

    companion object {
        fun newInstance() = HomeFrag()
    }

    //https://developer.android.com/kotlin/ktx
    val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter:onlineAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = onlineAdapter(requireContext())
        binding.familiarRV.layoutManager = LinearLayoutManager(this.context)
        binding.familiarRV.adapter = adapter
        observerData()
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        //툴바 filter item 보이게 하기
        menu.findItem(R.id.filterItem).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this



    }

    fun observerData(){
        viewModel.fetchData().observe(this, Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }


    // 사용자가 프래그먼트를 떠나면 첫번째로 onPause()를 호출합니다.
    // 사용자가 돌아오지 않을 수 있으므로, 여기에 현재 사용자 세션을 넘어 지속되어야하는 변경사항을 저장.
    override fun onPause() {
        super.onPause()
    }



}
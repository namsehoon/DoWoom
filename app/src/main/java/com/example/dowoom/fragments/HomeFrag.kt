package com.example.dowoom.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.Adapter.UserBindAdapter
import com.example.dowoom.Adapter.onlineAdapter
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel
import com.example.dowoom.R
import com.example.dowoom.databinding.HomeFragmentBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFrag : BaseFragment<HomeFragmentBinding>(TAG = "HomeFrag", R.layout.home_fragment) {

    companion object {
        fun newInstance() = HomeFrag()
    }

    //https://developer.android.com/kotlin/ktx
    val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: onlineAdapter

    override fun onPrepareOptionsMenu(menu: Menu) {
        //툴바 filter item 보이게 하기
        menu.findItem(R.id.filterItem).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //어뎁터
        adapter = onlineAdapter(requireActivity())
        binding.onlineRV.layoutManager = LinearLayoutManager(this.context)
        binding.onlineRV.adapter = adapter
    }

    //view 변경 작업
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //바인딩
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()


        //옵저버 (back key 에러를 보완하기 위해서 여기서 getlifecyclerowner로 관찰)
        observerData()

        var auth = Firebase.auth
        var firebaseUser: FirebaseUser? = auth.currentUser
        Log.d("abcd", "firebaseuser is in homefragment : "+firebaseUser?.uid)
    }

    fun observerData() {
        //어뎁터 설정
        //observe

        viewModel.observeUser().observe(viewLifecycleOwner, Observer {
            Log.d("abcd","it is ;" +it)
            adapter.setUser(it)
        })
    }


    // 사용자가 프래그먼트를 떠나면 첫번째로 onPause()를 호출합니다.
    // 사용자가 돌아오지 않을 수 있으므로, 여기에 현재 사용자 세션을 넘어 지속되어야하는 변경사항을 저장.
    override fun onPause() {
        super.onPause()
    }



}
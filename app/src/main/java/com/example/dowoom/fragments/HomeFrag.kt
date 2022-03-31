package com.example.dowoom.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.adapter.HomeAdapter
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel
import com.example.dowoom.R
import com.example.dowoom.activity.chatRoom.ChatRoomActivity
import com.example.dowoom.databinding.HomeFragmentBinding
import com.example.dowoom.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFrag : BaseFragment<HomeFragmentBinding>(TAG = "HomeFrag", R.layout.home_fragment) {

    companion object {
        fun newInstance() = HomeFrag()
    }


    //https://developer.android.com/kotlin/ktx
    val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: HomeAdapter

    override fun onPrepareOptionsMenu(menu: Menu) {
        //툴바 filter item 보이게 하기
        menu.findItem(R.id.filterItem).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //어뎁터
        adapter = HomeAdapter(requireActivity(),
            profileClick = { user ->
                //todo 프로필 이미지 추가

        },
        talkClick = { user ->
            //todo 프로필 이미지 추가
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("대화생성")
                .setView(binding.root)
                .create()
            alertDialog.show()

            CoroutineScope(Dispatchers.Main).launch {
                viewModel.insertChat(user.nickname!!, user.uid!!)
            }

            alertDialog.dismiss()

            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("fromUid",user.uid)
            intent.putExtra("fromNickname", user.nickname)
            context?.startActivity(intent)



        })
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
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.observeUser().observe(viewLifecycleOwner, Observer {
                Log.d("abcd","it user is : "+it.toString())
                adapter.setUser(it)
            })

        }
    }


    // 사용자가 프래그먼트를 떠나면 첫번째로 onPause()를 호출합니다.
    // 사용자가 돌아오지 않을 수 있으므로, 여기에 현재 사용자 세션을 넘어 지속되어야하는 변경사항을 저장.
    override fun onPause() {
        super.onPause()
    }



}
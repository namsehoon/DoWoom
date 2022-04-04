package com.example.dowoom.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.adapter.HomeAdapter
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel
import com.example.dowoom.R
import com.example.dowoom.activity.chat.ChatActivity
import com.example.dowoom.databinding.HomeFragmentBinding
import com.example.dowoom.Util.CustomAlertDialog
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
            val alertDialog = CustomAlertDialog(this.requireActivity())
            alertDialog.start(user.nickname.plus("님과 대화하시겠습니까?"))
            //대화생성 ok 클릭 시,
            alertDialog.onOkClickListener(object : CustomAlertDialog.onDialogCustomListener {
                override fun onClicked() {
                    CoroutineScope(Dispatchers.Main).launch {


                        //채팅방 ac으로 이동
                        val intent = Intent(context, ChatActivity::class.java)
                        //todo : 만약 존재하면 그 채팅방으로 가야됨
                        viewModel.checkedChat(user)
                        //상대방 uid
                        intent.putExtra("otherUid",user.uid)
                        //상대방 nickname
                        intent.putExtra("otherNickname", user.nickname)
                        intent.putExtra("profileImg", user.profileImg)
                        context?.startActivity(intent)

                    }
                }
            })

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

    }


    fun observerData() {
        //어뎁터 설정
        //observe
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeUser().observe(viewLifecycleOwner, Observer {
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
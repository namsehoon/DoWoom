package com.example.dowoom.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.activity.profile.ShowProfileActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class HomeFrag : BaseFragment<HomeFragmentBinding>(TAG = "HomeFrag", R.layout.home_fragment) {

    companion object {
        fun newInstance() = HomeFrag()
    }

    //https://developer.android.com/kotlin/ktx
    val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: HomeAdapter

    private lateinit var intent: Intent

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
                val intent = Intent(context,ShowProfileActivity::class.java)
                intent.putExtra("partnerId", user.uid)
                intent.putExtra("partnerAge",user.age)
                intent.putExtra("partnerPopularity",user.popularity)
                intent.putExtra("partnerSOrB",user.sOrB)
                intent.putExtra("partnerStateMsg",user.stateMsg)
                //상대방 nickname
                intent.putExtra("partnerNickname", user.nickname)
                intent.putExtra("profileImg", user.profileImg)

                startActivity(intent)
        },
        talkClick = { user ->
            CoroutineScope(Dispatchers.Main).launch {


            //채팅방 ac으로 이동
            intent = Intent(context, ChatActivity::class.java)

            //상대방 uid
            intent.putExtra("partnerId", user.uid)
            intent.putExtra("partnerAge",user.age)
            //상대방 nickname
            intent.putExtra("partnerNickname", user.nickname)
            intent.putExtra("profileImg", user.profileImg)

            val alertDialog = CustomAlertDialog(requireActivity())
            alertDialog.start(user.nickname.plus("님과 대화를 시작하시겠습니까?"))
            //대화생성 ok 클릭 시,
            alertDialog.onOkClickListener(object : CustomAlertDialog.onDialogCustomListener {
                override fun onClicked() {
                    context?.startActivity(intent)
                }

                override fun onCanceled() {
                    TODO("Not yet implemented")
                }
            })
        }

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
                Log.d("Abcd","home is : ${it}")
                adapter.setUser(it)
            })

            //참고 : https://stackoverflow.com/questions/50000975/am-i-allowed-to-observe-a-viewmodel-if-i-clean-up-the-back-references
            viewModel.getOnEndLive().observe(viewLifecycleOwner, Observer { onEnd ->
                if (onEnd != null && onEnd == true) {
//                    context?.startActivity(intent)
                }
            })


        }
    }


    // 사용자가 프래그먼트를 떠나면 첫번째로 onPause()를 호출합니다.
    // 사용자가 돌아오지 않을 수 있으므로, 여기에 현재 사용자 세션을 넘어 지속되어야하는 변경사항을 저장.
    override fun onPause() {
        super.onPause()
    }



}
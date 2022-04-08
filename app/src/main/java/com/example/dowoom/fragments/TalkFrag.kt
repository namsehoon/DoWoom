package com.example.dowoom.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.activity.chat.ChatActivity
import com.example.dowoom.adapter.ChatRoomAdapter
import com.example.dowoom.databinding.TalkFragmentBinding
import com.example.dowoom.viewmodel.mainViewmodel.TalkViewModel
import kotlinx.coroutines.launch

class TalkFrag : BaseFragment<TalkFragmentBinding>("TalkFrag", R.layout.talk_fragment) {


    companion object {
        fun newInstance() = TalkFrag()
    }

    val viewModel by viewModels<TalkViewModel>()
    private lateinit var adapter: ChatRoomAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //어뎁터
        adapter = ChatRoomAdapter(requireActivity(),
            goIntoChatroom = { chatRoom ->
                //채팅방 클릭시, 채팅방 내로
                val intent = Intent(context, ChatActivity::class.java)
                //todo : 만약 존재하면 그 채팅방으로 가야됨
                //채팅방 uid
                intent.putExtra("chatUid",chatRoom.chatUid)
                intent.putExtra("otherUid", chatRoom.otherUid)
                intent.putExtra("otherNickname", chatRoom.nickname)

                context?.startActivity(intent)
            })

        binding.rvChatroom.layoutManager = LinearLayoutManager(this.context)
        binding.rvChatroom.adapter = adapter
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()

        observerData()
    }

    fun observerData() {
        //대화 recycler 만들기
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeChat().observe(viewLifecycleOwner, Observer { it ->
                Log.d("abcd","it chat is : "+it.toString())
                adapter.setChatroom(it)
            })
        }
    }



}
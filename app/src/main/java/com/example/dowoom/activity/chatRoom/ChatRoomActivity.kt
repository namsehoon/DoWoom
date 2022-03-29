package com.example.dowoom.activity.chatRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityChatRoomBinding
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.example.dowoom.viewmodel.loginViewmodel.LoginViewmodel

class ChatRoomActivity : BaseActivity<ActivityChatRoomBinding>(TAG = "채팅룸", R.layout.activity_chat_room) {

    val viewModel: ChatViewmodel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }
}
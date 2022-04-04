package com.example.dowoom.activity.chat

import android.os.Bundle
import androidx.activity.viewModels
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityChatBinding
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : BaseActivity<ActivityChatBinding>(TAG = "채팅룸", R.layout.activity_chat) {

    val viewModel: ChatViewmodel by viewModels()

    var myUid:String? = null
    var chatUid:String? = null
    var otherUid:String? = null
    var otherNickname:String? = null
    var profileImg:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //todo : 1. 어차피 메세지 들은 db에서 가져온다.
        //todo : 2. adapter를 사용하여 button 클릭시, 메세지 추가해준다.
        //todo : 3. 어떻게 상대방인지 아냐? :
        //todo : 4.
    }

    fun initialized() {
        val i = intent

        if (FirebaseAuth.getInstance().currentUser != null) {
            myUid = FirebaseAuth.getInstance().currentUser?.uid
        }
        otherUid = i.getStringExtra("otherUid")
        otherNickname = i.getStringExtra("otherNickname")

        if (i.hasExtra("chatUid")) {
            chatUid = i.getStringExtra("chatUid")
        }
        if (i.hasExtra("profileImg")) {
            profileImg = i.getStringExtra("profileImg")
        }
    }
}
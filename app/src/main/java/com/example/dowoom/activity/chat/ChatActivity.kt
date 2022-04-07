package com.example.dowoom.activity.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.ChatRoomAdapter
import com.example.dowoom.adapter.chatMsgAdatper

import com.example.dowoom.databinding.ActivityChatBinding
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ActivityChatBinding>(TAG = "채팅룸", R.layout.activity_chat) {

    val viewModel: ChatViewmodel by viewModels()
    private lateinit var adapter: chatMsgAdatper


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

        initialized()
        initialViewModel()
    }

    fun initialViewModel() {
        lifecycleScope.launch {
            viewModel.observeMessage(otherUid!!,chatUid!!).observe(this@ChatActivity ,Observer {
                Log.d("Abcd"," it value in chatAC is : $it")
            })
        }
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

        adapter = chatMsgAdatper(this@ChatActivity, chatUid!!, myUid!!, msgClicked =  { message ->
            //클릭시 삭제
            val dialog = CustomAlertDialog(this@ChatActivity)
            dialog.start("삭제하시겠습니까? (상대방에게서도 삭제됨)")
            dialog.onOkClickListener(object : CustomAlertDialog.onDialogCustomListener {
                override fun onClicked() {
                    Log.d("abcd","삭제 확인 누름")
                    //todo : 삭제
                }
            })
        })


    }
}
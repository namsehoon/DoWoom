package com.example.dowoom.activity.chat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.chatMsgAdatper

import com.example.dowoom.databinding.ActivityChatBinding
import com.example.dowoom.model.Message
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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


        initialized()
        initialViewModel()

        lifecycleScope.launchWhenResumed {
            //메세지 보내기
            binding.ivSendMsg.setOnClickListener {
                val message = viewModel.etMessage.value.toString()
                if (!message.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                         viewModel.insertMessage(chatUid!!,message,myUid!!,otherUid!!)
                    }

                } else {
                    Toast.makeText(this@ChatActivity,"메세지를 입력해주세요.",Toast.LENGTH_SHORT).show()
                }
            }
            //이미지 보내기
            binding.ivSendMsg.setOnClickListener {

            }

        }
    }

    fun initialViewModel() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.observeMessage(otherUid!!,chatUid!!).observe(this@ChatActivity , Observer { it ->
                Log.d("Abcd"," it value in chatAC is : $it")
                adapter.setMessage(it)

            })

            viewModel.message.observe(this@ChatActivity, Observer { result ->
                Log.d("abcd","result is ${result}")
                adapter.addMessage(result)
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
        Log.d("abcd","chatUid in ChatAC is : ${chatUid} and myuid is : ${myUid}")



        //어뎁터 설정
        adapter = chatMsgAdatper(this@ChatActivity, chatUid!!, myUid!!, msgClicked =  { message, position ->
            //메세지 삭제
            val dialog = CustomAlertDialog(this@ChatActivity)
            dialog.start("삭제하시겠습니까? (상대방에게서도 삭제됨)")
            dialog.onOkClickListener(object : CustomAlertDialog.onDialogCustomListener {
                override fun onClicked() {
                    Log.d("abcd","삭제 확인 누름")
                   CoroutineScope(Dispatchers.IO).launch {
                       viewModel.deleteMessage(chatUid!!,message.messageId!!,message.otherUid!!,message.timeStamp!!,message.sender!!)
                       withContext(Dispatchers.Main) {
                           message.message = "삭제되었습니다."
                           adapter.notifyItemChanged(position)
                       }
                   }

                }
            })
        })
        binding.rvChatRoom.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding.rvChatRoom.adapter = adapter

    }


}
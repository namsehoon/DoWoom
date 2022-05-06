package com.example.dowoom.activity.chat

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.chatMsgAdatper

import com.example.dowoom.databinding.ActivityChatBinding
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ChatActivity : BaseActivity<ActivityChatBinding>(TAG = "채팅룸", R.layout.activity_chat) {

    val viewModel: ChatViewmodel by viewModels()
    private lateinit var adapter: chatMsgAdatper

    //todo global하게 쓰이는 변수, 클래스들 null처리 해줘야 함.
    var myUid:String? = null
    var otherUid:String? = null
    var otherNickname:String? = null
    var profileImg:String? = null

    //start result for activity
    val TAKE_IMAGE_CODE = 10001

    override fun onDestroy() {
        super.onDestroy()
        myUid = null
        otherUid = null
        otherNickname = null
        profileImg = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()
        initialViewModel()

        lifecycleScope.launchWhenResumed {
            //메세지 보내기

            binding.ivSendMsg.setOnClickListener {
                Log.d("abcd","클릭됨")

                val message = viewModel.etMessage.value.toString()
                if (!message.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.insertMessage(null ,message, myUid!!,otherUid!!,otherNickname!!)
                    }
                } else {
                    binding.ivSendMsg.isEnabled = false
                    Toast.makeText(this@ChatActivity,"메세지를 입력해주세요.",Toast.LENGTH_SHORT).show()
                }
            }


            //todo 이미지 보내기
            binding.ivSendImg.setOnClickListener {
                //권한 체크
                PermissionCheck(this@ChatActivity).checkPermission()

                val mimeType = arrayOf<String>("image/jpeg","image/png")

                val intent = Intent(Intent.ACTION_PICK)
                    .apply { type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES,mimeType)
                    }

                this@ChatActivity.startActivityForResult(Intent.createChooser(intent, "앨범 가져오기"),TAKE_IMAGE_CODE)

            }
        }

    }

    fun initialViewModel() {

        lifecycleScope.launchWhenResumed {

            viewModel.observeMessage().observe(this@ChatActivity , Observer { it ->
                Log.d("Abcd"," it value in chatAC is : $it")
                adapter.setMessage(it)

            })

            //메세지 추가
            viewModel.message.observe(this@ChatActivity, Observer { result ->
                Log.d("abcd","result is ${result}")
                adapter.addMessage(result)
                binding.rvChatRoom.scrollToPosition(adapter.messages.size -1)
            })


        }
    }


    fun initialized() {
        val i = intent

        //내 uids
        myUid = FirebaseAuth.getInstance().currentUser?.uid
        //상대방 uid
        otherUid = i.getStringExtra("otherUid")
        Log.d("abcd","otheruid is : ${otherUid}")
        //상대방 닉네임
        otherNickname = i.getStringExtra("otherNickname")
        //상대방 img
        profileImg = i.getStringExtra("profileImg")

        Log.d("abcd","otheruid in chatactivity is : ${otherUid}")
        //chatid 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getChatId(otherUid!!)
        }

        //어뎁터 설정
        adapter = chatMsgAdatper(this@ChatActivity, msgClicked =  { message, position ->
            //메세지 삭제
            val dialog = CustomAlertDialog(this@ChatActivity)
            dialog.start("삭제하시겠습니까? (상대방에게서도 삭제됨)")
            dialog.onOkClickListener(object : CustomAlertDialog.onDialogCustomListener {
                override fun onClicked() {
                    Log.d("abcd","삭제 확인 누름")
                   CoroutineScope(Dispatchers.IO).launch {
                       viewModel.deleteMessage(message.messageId!!,message.otherUid!!,message.timeStamp!!,message.sender!!,viewModel.chatId.value.toString())
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE) {
            val context = this@ChatActivity
            var cursor:Cursor? = null

            when(resultCode) {
                RESULT_OK -> {
                    try {
                        // uri 스키마를 content:///에서 file:///로변경 (절대경로)
                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        val uri:Uri = data?.data!!

                        cursor = context.contentResolver.query(uri, proj, null, null, null)

                        val column_index: Int =
                            cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)!!

                        cursor.moveToFirst()

                        val tempFile = File(cursor.getString(column_index))

                        Log.d("abcd","file path is : ${tempFile.absolutePath}")

                        //사진 to db
                        CoroutineScope(Dispatchers.Main).launch {
                            context.viewModel.insertMessage(tempFile.absolutePath,"photo",myUid!!,otherUid!!,otherNickname!!)
                        }

                    } finally {
                        cursor?.close()
                    }

                }
                else -> {
                    Toast.makeText(context, "사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}
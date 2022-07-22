package com.example.dowoom.activity.chat

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.collection.arraySetOf
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.Util.CustomProgressDialog
import com.example.dowoom.Util.HandleImage
import com.example.dowoom.Util.PermissionCheck
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.chatMsgAdatper

import com.example.dowoom.databinding.ActivityChatBinding
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.talkModel.Message
import com.example.dowoom.repo.ChatRepo
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.google.firebase.auth.FirebaseAuth
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.IOException

class ChatActivity : BaseActivity<ActivityChatBinding>(TAG = "채팅룸", R.layout.activity_chat) {

    val viewModel: ChatViewmodel by viewModels()
    private lateinit var adapter: chatMsgAdatper

    var partnerId:String? = null
    var partnerNickname:String? = null
    var profileImg:String? = null

    //start result for activity
    val TAKE_IMAGE_CODE = 10001
    //start result for activity
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    //이미지 리스트
    var uriList : MutableList<Uri> = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.vm = viewModel
        binding.lifecycleOwner = this


        initialized()


        lifecycleScope.launchWhenResumed {
            //메세지 보내기

            binding.ivSendMsg.setOnClickListener {

                val message = viewModel.etMessage.value.toString()
                Log.d("Abcd","insertMessage - message is : $message")
                if (!message.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.insertMessage( message, Ref().auth.uid,partnerId!!)
                    }
                } else {
                    binding.ivSendMsg.isEnabled = false
                    Toast.makeText(this@ChatActivity,"메세지를 입력해주세요.",Toast.LENGTH_SHORT).show()
                }
            }


            binding.ivSendImg.setOnClickListener {
                //권한 체크
                PermissionCheck(this@ChatActivity).checkPermission()

                val mimeType = arraySetOf<MimeType>(MimeType.JPEG, MimeType.PNG)
                //이미지 선택기 라이브러리
                Matisse.from(this@ChatActivity)
                    .choose(mimeType) //이미지 타입
                    .countable(true) //이미지 카운터
                    .maxSelectable(6) //이미지 선택 최대 개수
                    .thumbnailScale(1f)
                    .imageEngine(GlideEngine())
                    .showPreview(false)
                    .forResult(TAKE_IMAGE_CODE)


            }
        }

        //start for result
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

            })

    }



    fun initialViewModel() {

        lifecycleScope.launchWhenResumed {

            ChatRepo().observeMessage(Ref().auth.uid,partnerId!!).observe(this@ChatActivity, Observer {
                adapter.addMessage(it)
            })

            ChatRepo().observeMessage(partnerId!!,Ref().auth.uid).observe(this@ChatActivity, Observer {
                adapter.addMessage(it)
            })

            //메세지 추가
            viewModel.message.observe(this@ChatActivity, Observer { result ->
                Log.d("abcd","result is ${result}")
                adapter.addMessage(result)
                binding.rvChatRoom.scrollToPosition(adapter.messages.size -1)
            })
            //todo 사진 저장

            viewModel.memberCheck.observe(this@ChatActivity, Observer { result ->
                if (result) { // 둘 중 하나가 null 이라면,
                     binding.ivSendMsg.isEnabled = false
                    binding.etMessage.isEnabled = false
                }
            })
        }
    }


    fun initialized() {
        val i = intent


        //상대방 uid
        partnerId = i.getStringExtra("partnerId")
        //상대방 닉네임
        partnerNickname = i.getStringExtra("partnerNickname")
        //상대방 img
        profileImg = i.getStringExtra("profileImg")


        Log.d("Abcd","partnerId : $partnerId")

        //어뎁터 설정
        adapter = chatMsgAdatper(this@ChatActivity, msgClicked =  { message, position ->
            //메세지 삭제
            val dialog = CustomAlertDialog(this@ChatActivity)
            dialog.start("삭제하시겠습니까?\n(상대방에게서도 삭제됨)")
            dialog.onOkClickListener(object : CustomAlertDialog.onDialogCustomListener {
                override fun onClicked() {
                    Log.d("abcd","삭제 확인 누름")
                   CoroutineScope(Dispatchers.IO).launch {
                       Log.d("abcd","\"클릭된 id : ${message.messageId}\"")
                       Log.d("abcd","\"클릭된 message : ${message.message}\"")
//                       viewModel.deleteMessage(message.messageId!!)
                       withContext(Dispatchers.Main) {
                           //그냥 position에 박아버리는 듯. 다른건 뒤로 밀려버리고
                           message.message = "삭제되었습니다. activity"
                           adapter.notifyItemChanged(position)
                       }
                   }

                }
            })
        }, profileImg)
        binding.rvChatRoom.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding.rvChatRoom.adapter = adapter

        initialViewModel()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            uriList = Matisse.obtainResult(data)

            if (uriList.isEmpty()) {
                Toast.makeText(this@ChatActivity, "사진을 선택해 주세요.",Toast.LENGTH_SHORT).show()
            } else {
                //사진 to db
                CoroutineScope(Dispatchers.IO).launch {
                    HandleImage(uriList, Ref().auth.uid,partnerId!!).handleUpload()
                }
            }
        }
    }

}
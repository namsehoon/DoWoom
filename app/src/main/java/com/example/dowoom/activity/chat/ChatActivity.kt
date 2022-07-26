package com.example.dowoom.activity.chat

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.insertImage
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.collection.arraySetOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.*
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.adapter.chatMsgAdatper

import com.example.dowoom.databinding.ActivityChatBinding
import com.example.dowoom.firebase.Ref
import com.example.dowoom.repo.ChatRepo
import com.example.dowoom.viewmodel.chatviewmodel.ChatViewmodel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import android.R.attr.bitmap
import android.graphics.BitmapFactory


class ChatActivity : BaseActivity<ActivityChatBinding>(TAG = "채팅룸", R.layout.activity_chat) {

    val viewModel: ChatViewmodel by viewModels()
    private lateinit var adapter: chatMsgAdatper

    var partnerId:String? = null
    var partnerNickname:String? = null
    var profileImg:String? = null
    var partnerAge:Int? = null

    val ONE_MEGABYTE: Long = 1024 * 1024
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

            viewModel.blockCheck.observe(this@ChatActivity, Observer { blocked ->
                if (blocked) {
                    val dialog = CustomBlockDialog(this@ChatActivity!!)
                    dialog.start("상대방과 대화 할 수 없습니다. \n (사유: 차단 등)")
                    dialog.onOkClickListener(object : CustomBlockDialog.onDialogCustomListener {
                        override fun onClicked() {
                            finish()
                        }

                    })
                }

            })


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


        viewModel.observeBlock(partnerId!!)

        Log.d("Abcd","partnerId : $partnerId")

        //어뎁터 설정
        adapter = chatMsgAdatper(this@ChatActivity, imgClicked =  { message, position ->
            //이미지 클릭 시,
            Log.d("Abcd","here i am")
            val imageDialog = ImageSaveDialog(this@ChatActivity)
            imageDialog.start(message.imageUrl!!)
            imageDialog.onOkClickListener(object : ImageSaveDialog.onDialogCustomListener{
                override fun onClicked() {
                    Log.d("Abcd","저장버튼 클릭 됨")

                    saveIMG(message.imageUrl!!)
                }

            })

        }, profileImg)
        binding.rvChatRoom.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding.rvChatRoom.adapter = adapter

        initialViewModel()

    }

    fun saveIMG(imgPath:String) {
        PermissionCheck(this@ChatActivity).checkPermission()
        CoroutineScope(Dispatchers.IO).launch {
            val imageRef = Ref().storage.getReferenceFromUrl(imgPath) //사진 ref
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it,0,it.size) //bytes -> bitmap
                SaveImage().imageExternalSave(this@ChatActivity,bitmap,"DoWoom")
            }
                .addOnFailureListener {
                    Log.d("abcd","ChatActivity - image 저장실패 : ${it.message}")
            }

        }
        Toast.makeText(this@ChatActivity,"사진 저장이 완료 되었습니다.",Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            uriList = Matisse.obtainResult(data)

            if (uriList.isEmpty()) {
                Toast.makeText(this@ChatActivity, "사진을 선택해 주세요.",Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    HandleImage(uriList, Ref().auth.uid,partnerId!!).handleUpload()
                }
            }
        }
    }

}
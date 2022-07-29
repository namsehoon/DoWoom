package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.R
import com.example.dowoom.Util.GlideApp
import com.example.dowoom.Util.TimeStampToDate
import com.example.dowoom.databinding.ItemTalkBinding
import com.example.dowoom.model.talkModel.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ChatRoomAdapter(val context: Context, val goIntoChatroom:(ChatRoom) -> Unit, val chatClicked:(ChatRoom, position:Int) -> Unit) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {


    val diffUtil = AsyncListDiffer(this,ChatRoomDiffUtil())
    //유저
    var chatRooms = mutableListOf<ChatRoom>()

    fun setChatroom(chatrooms:MutableList<ChatRoom>) {
        diffUtil.submitList(chatrooms)
        Log.d("abcd","chatrooms is :"+chatrooms.toString())
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_talk,parent,false)
        return ChatRoomViewHolder(view)

    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.ChatRoomViewHolder, position: Int) {
        //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        val chatroom = diffUtil.currentList[position]


        if (chatroom.user?.profileImg.isNullOrEmpty()) {
            val res = R.drawable.ic_baseline_person_24
            holder.chatRoomBinding.profileImg.setImageDrawable(context.getDrawable(res))
        } else {
            GlideApp.with(context)
                .load(chatroom.user?.profileImg) // 이미지를 로드
                .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
                .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
                .into(holder.chatRoomBinding.profileImg) //이미지를 보여줄 view를 지정
        }

        if (chatroom.message.equals("이미지")) {
            holder.chatRoomBinding.tvLastMsg.text = "이미지"
        } else {
            holder.chatRoomBinding.tvLastMsg.text = chatroom.message.toString()
        }

        holder.chatRoomBinding.tvLastTime.text = TimeStampToDate().getDate(chatroom.date!!)

        holder.chatRoomBinding.tvNickname.text = chatroom.user?.nickname.toString()


        //채팅방 클릭 시, 채팅방 내에 들어가기
        holder.chatRoomBinding.llChatroom.setOnClickListener {
            goIntoChatroom(chatroom)
        }


        //채팅방 길게 클릭 시, 삭제
        holder.itemView.setOnLongClickListener {
            chatClicked(chatroom,position)
            false
        }

        holder.chatRoomBinding.executePendingBindings()

    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    inner class ChatRoomViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var chatRoomBinding: ItemTalkBinding = ItemTalkBinding.bind(itemView)
    }



}
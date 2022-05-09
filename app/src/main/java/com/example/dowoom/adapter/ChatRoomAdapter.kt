package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.databinding.ItemTalkBinding
import com.example.dowoom.model.ChatRoom

class ChatRoomAdapter(val context: Context, val goIntoChatroom:(ChatRoom) -> Unit, val chatClicked:(ChatRoom,position:Int) -> Unit) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    //유저
    var chatRooms = mutableListOf<ChatRoom>()


    fun setChatroom(chatrooms:MutableList<ChatRoom>) {
        Log.d("abcd","chatrooms is :"+chatrooms.toString())
        chatRooms.clear()
        chatRooms.addAll(chatrooms)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_talk,parent,false)
        return ChatRoomViewHolder(view)

    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.ChatRoomViewHolder, position: Int) {
        //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        val chatroom = chatRooms[position]

//todo
//        if (chatroom.profileImg != null) {
//            Glide.with(context)
//                .load(chatroom.profileImg) // 이미지를 로드
//                .override(100,100)
//                .placeholder(R.drawable.ic_baseline_person_24) // 이미지로딩을 시작하기전에 보여줄 이미지
//                .error(R.drawable.ic_baseline_person_24) // 불러오다가 에러발생
//                .fallback(R.drawable.ic_baseline_person_24) // 이미지가 null
//                .into(holder.chatRoomBinding.profileImg) //이미지를 보여줄 view를 지정
//        } else {
//            Glide.with(context)
//                .load(chatroom.profileImg) // 이미지를 로드
//                .override(100,100)
//                .placeholder(R.drawable.ic_baseline_person_24) // 이미지로딩을 시작하기전에 보여줄 이미지
//                .error(R.drawable.ic_baseline_person_24) // 불러오다가 에러발생
//                .fallback(R.drawable.ic_baseline_person_24) // 이미지가 null
//                .into(holder.chatRoomBinding.profileImg) //이미지를 보여줄 view를 지정
//        }

        if (chatroom.lastMessage.toString() == "photo") {
            holder.chatRoomBinding.tvLastMsg.text = "사진"
        } else {
            holder.chatRoomBinding.tvLastMsg.text = chatroom.lastMessage.toString()
        }

        holder.chatRoomBinding.tvLastTime.text = chatroom.timeStamp.toString()
        holder.chatRoomBinding.newMsg.text = "채워"
        holder.chatRoomBinding.tvNickname.text = chatroom.nickname.toString()


        //채팅방 클릭 시, 채팅방 내에 들어가기
        holder.chatRoomBinding.llChatroom.setOnClickListener {
            goIntoChatroom(chatRooms[position])
        }

        //todo : 생각해보니까 상대방은 채팅방 어떻게 들어옴?

        //채팅방 길게 클릭 시, 삭제
        holder.itemView.setOnLongClickListener {
            chatClicked(chatroom,position)
            false
        }

        holder.chatRoomBinding.executePendingBindings()

    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    inner class ChatRoomViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var chatRoomBinding: ItemTalkBinding = ItemTalkBinding.bind(itemView)
    }



}
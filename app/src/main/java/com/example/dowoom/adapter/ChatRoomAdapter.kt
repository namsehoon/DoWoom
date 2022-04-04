package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.R
import com.example.dowoom.databinding.ItemTalkBinding
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.User

class ChatRoomAdapter(val context: Context, val goIntoChatroom:(ChatRoom) -> Unit ) : RecyclerView.Adapter<ChatRoomAdapter.Viewholder>() {

    //바인딩
    private lateinit var binding: ItemTalkBinding
    //유저
    var chatRooms = mutableListOf<ChatRoom>()

    //onclicklistener은 view에서 구현하는것이 바람직하다고 합니다.(viewModel 사용 또는 fragment간 화면전환 )
    interface OnItemClickListener {
        fun onItemClick(chatRoom: ChatRoom)
    }


    fun setChatroom(chatrooms:MutableList<ChatRoom>) {
        Log.d("abcd","chatrooms is :"+chatrooms.toString())
        chatRooms.clear()
        chatRooms.addAll(chatrooms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.Viewholder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_talk,parent,false)
        return Viewholder(binding)

    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.Viewholder, position: Int) {
        //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        holder.bind(chatRooms,position)

        //채팅방 클릭 시,
        holder.binding.llChatroom.setOnClickListener {
            goIntoChatroom(chatRooms[position])
        }

        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    inner class Viewholder(val binding: ItemTalkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatrooms:List<ChatRoom>, position:Int) {
            binding.chatItem = chatrooms[position]
        }
    }
}
package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.model.User
import com.example.dowoom.R
import com.example.dowoom.databinding.ItemRecyclerBinding
//private val itemClickListener:(User) -> Unit
class HomeAdapter(val context: Context,val profileClick:(User) -> Unit, val talkClick:(User) -> Unit) : RecyclerView.Adapter<HomeAdapter.Viewholder>() {

    //바인딩
    private lateinit var binding: ItemRecyclerBinding
    //유저
    var users = mutableListOf<User>()

    //onclicklistener은 view에서 구현하는것이 바람직하다고 합니다.(viewModel 사용 또는 fragment간 화면전환 )
    interface OnItemClickListener {
        fun onItemClick(user:User)
    }


    //유저 셋
    fun setUser(user:MutableList<User>) {
        //유저 데이터를 불러 올 때, 데이터가 바뀌면 before데이터랑 after 데이터를 다불러와서
        users.clear()
        users.addAll(user)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.Viewholder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_recycler,parent,false)

        return Viewholder(binding)

    }

    override fun onBindViewHolder(holder: HomeAdapter.Viewholder, position: Int) {
       //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        holder.bind(users,position)

        //대화 시작
        holder.binding.llHomeChat.setOnClickListener {
            talkClick(users[position])
        }
        // 프로필
        holder.binding.llImage.setOnClickListener {
            profileClick(users[position])
        }

        //This is to force bindings to execute right away
        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int {
       return users.size
    }

    inner class Viewholder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(users:MutableList<User>, position:Int) {
            binding.userItem = users[position]

        }


    }

}
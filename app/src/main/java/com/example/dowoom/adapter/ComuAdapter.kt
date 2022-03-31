package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.R
import com.example.dowoom.databinding.ItemRecyclerBinding
import com.example.dowoom.model.User

class ComuAdapter(val context: Context) : RecyclerView.Adapter<ComuAdapter.Viewholder>() {

    //바인딩
    private lateinit var binding: ItemRecyclerBinding
    //유저
    var users = mutableListOf<User>()

    fun setUser(user:MutableList<User>) {
        Log.d("abcd","user is :"+user.toString())
        //유저 데이터를 불러 올 때, 데이터가 바뀌면 before데이터랑 after 데이터를 다불러와서
        users.clear()
        users.addAll(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComuAdapter.Viewholder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_talk,parent,false)
        return Viewholder(binding)

    }

    override fun onBindViewHolder(holder: ComuAdapter.Viewholder, position: Int) {
        //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        Viewholder(binding).bind(users, position)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class Viewholder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(users:List<User>, position:Int) {
            binding.userItem = users[position]
        }
    }
}
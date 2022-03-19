package com.example.dowoom.Adapter

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.BR
import com.example.dowoom.model.User
import com.example.dowoom.R
import com.example.dowoom.databinding.ItemRecyclerBinding
import com.example.dowoom.viewmodel.mainViewmodel.HomeViewModel

class onlineAdapter(val context: Context) : RecyclerView.Adapter<onlineAdapter.Viewholder>() {

    //바인딩
    private lateinit var binding: ItemRecyclerBinding
    //유저
    var users = mutableListOf<User>()

    fun setUser(user:MutableList<User>) {
        Log.d("abcd","user is :"+user.toString())
        //유저 데이터를 불러 올 때, 데이터가 바뀌면 before데이터랑 after 데이터를 다불러와서
        users.clear()
        users.addAll(user)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onlineAdapter.Viewholder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_recycler,parent,false)
        return Viewholder(binding)

    }

    override fun onBindViewHolder(holder: onlineAdapter.Viewholder, position: Int) {
       //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        binding.userItem = users[position]
    }

    override fun getItemCount(): Int {
       return users.size
    }

    inner class Viewholder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
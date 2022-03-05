package com.example.dowoom.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.Model.User
import com.example.dowoom.R

class onlineAdapter(private val context:Context) : RecyclerView.Adapter<onlineAdapter.Viewholder>() {

    //유저
    private var userList = mutableListOf<User>()

    //fragment 에서 관찰중인 userdata를 가져온다.
    fun setListData(data:MutableList<User>){
        userList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onlineAdapter.Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler,parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: onlineAdapter.Viewholder, position: Int) {
        //각 포지션에서 값을 가져오기
        val user : User = userList[position]
        holder.name.text = user.nickname
        holder.age.text = user.age.toString()
        holder.popularity.text = user.popularity
        holder.stateMsg.text = user.stateMsg
    }

    override fun getItemCount(): Int {
       return userList.size
    }

    inner class Viewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val name : TextView = itemView.findViewById(R.id.tvNickname)
        val age : TextView = itemView.findViewById(R.id.tvAge)
        val popularity : TextView = itemView.findViewById(R.id.tvPopularity)
        val stateMsg:TextView = itemview.findViewById(R.id.tvState)
    }
}
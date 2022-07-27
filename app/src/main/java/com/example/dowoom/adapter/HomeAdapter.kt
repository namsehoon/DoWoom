package com.example.dowoom.adapter

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.model.User
import com.example.dowoom.R
import com.example.dowoom.Util.GlideApp
import com.example.dowoom.databinding.ItemRecyclerBinding
//private val itemClickListener:(User) -> Unit
class HomeAdapter(val context: Context,val profileClick:(User) -> Unit, val talkClick:(User) -> Unit) : RecyclerView.Adapter<HomeAdapter.UserHolder>() {

    //diff
    private val diffUtil = AsyncListDiffer(this,UserDiffUtil())

    //onclicklistener은 view에서 구현하는것이 바람직하다고 합니다.(viewModel 사용 또는 fragment간 화면전환 )
    interface OnItemClickListener {
        fun onItemClick(user:User)
    }

    //유저 셋
    fun setUser(newUsers:MutableList<User>) {
        newUsers.reversed() //최신이 위로
        diffUtil.submitList(newUsers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler,parent,false)

        return UserHolder(view)

    }

    override fun onBindViewHolder(holder: HomeAdapter.UserHolder, position: Int) {
       //databinding한 useritem에 users의 positoin에 맞게 뿌려줌
        val user = diffUtil.currentList[position]
        Log.d("abcd","user : ${user}")

        val viewHolder = holder as UserHolder
        Log.d("abcd","user.profile : ${user.profileImg.toString()}")
        if (user.profileImg.isNullOrEmpty()) {
            val res = R.drawable.ic_baseline_person_24
            viewHolder.binding.imageView.setImageDrawable(context.getDrawable(res))

        } else {
            GlideApp.with(context)
                .load(user.profileImg) // 이미지를 로드
                .override(80,80)
                .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                .error(R.drawable.ic_baseline_person_24) // 불러오다가 에러발생
                .into(viewHolder.binding.imageView) //이미지를 보여줄 view를 지정
        }


        viewHolder.binding.tvAge.text = user.age.toString()
        viewHolder.binding.tvNickname.text = user.nickname
        viewHolder.binding.tvState.text = user.stateMsg.toString()
        viewHolder.binding.tvPopularity.text = user.popularity.toString()



        //대화 시작
        viewHolder.binding.llHomeChat.setOnClickListener {
            talkClick(user)
        }
        // 프로필
        viewHolder.binding.llImage.setOnClickListener {
            profileClick(user)
        }

        //This is to force bindings to execute right away
        viewHolder.binding.executePendingBindings()

    }


    override fun getItemCount(): Int {
       return diffUtil.currentList.size
    }

    inner class UserHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var binding:ItemRecyclerBinding = ItemRecyclerBinding.bind(itemView)
    }

}
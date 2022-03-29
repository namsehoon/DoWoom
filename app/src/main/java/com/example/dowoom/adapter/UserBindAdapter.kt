package com.example.dowoom.adapter

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.model.User

object UserBindAdapter {
    @SuppressLint("NotifyDataSetChanged")
    @JvmStatic
    @BindingAdapter("users")
    fun setBindPost(view:RecyclerView, users:LiveData<MutableList<User>> ) {
        view.adapter?.run {
            if(this is HomeAdapter) {
                users.value?.let { this.users = it} ?: {this.users = arrayListOf()}()
                this.notifyDataSetChanged()
            }
        }
    }
}
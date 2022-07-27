package com.example.dowoom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.databinding.*
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.model.talkModel.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference



class ComuAdapter(val context: Context
, val contentClicked:(ComuModel, position:Int) -> Unit): RecyclerView.Adapter<ComuAdapter.ComuHolder>() {

    private val diffUtil = AsyncListDiffer(this,ComuModelDiffUtil())

    /** 변수 */

    //comu set
    fun setContents(comuModelList: MutableList<ComuModel>) {
        diffUtil.submitList(comuModelList)
        Log.d("abcd","recyclerview - setcontents is : $comuModelList")

    }

    fun setGuestContents(comuModelList: MutableList<ComuModel>) {
        diffUtil.submitList(comuModelList)
        Log.d("abcd","recyclerview - setGuestContents is : $comuModelList")

    }

    fun setPoliceContents(comuModelList: MutableList<ComuModel>) {
        diffUtil.submitList(comuModelList)
        Log.d("abcd","recyclerview - setPoliceContents is : $comuModelList")


    }




    /** implementation */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComuAdapter.ComuHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comu_items, parent, false)
        return ComuHolder(view)
    }

    override fun onBindViewHolder(holder: ComuAdapter.ComuHolder, position: Int) {
        val comu = diffUtil.currentList[position]

            //todo kindof으로 각자 핸들 가능

//        viewHolder.comuBinding.tvComentCount.text = comu.commentCount.toString() ?: "카운터없음"
        holder.comuBinding.tvContentTitle.text = comu.title ?: "타이틀없음"
        holder.comuBinding.tvNickname.text = comu.creator ?: "닉없음"
        if (comu.commentCount != 0) {
            holder.comuBinding.tvCommentCount.text = comu.commentCount.toString()
        } else {
            holder.comuBinding.tvCommentCount.text = " "
        }
            //메세지 삭제

        holder.comuBinding.llContents.setOnClickListener {
            contentClicked(diffUtil.currentList[position], position)
        }
        holder.comuBinding.executePendingBindings()



    }


    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    /** binding */
    inner class ComuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var comuBinding: ComuItemsBinding = ComuItemsBinding.bind(itemView)
    }



}



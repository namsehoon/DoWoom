package com.example.dowoom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuItemsBinding
import com.example.dowoom.databinding.ReceivemsgItemBinding
import com.example.dowoom.databinding.SendmsgItemBinding
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.model.talkModel.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference



class ComuAdapter(val context: Context
): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    /** 변수 */


    //todo : 1. 부모 프래그먼트에서 리스트 뿌릴거임 해당 버튼에 대한.
    //todo : 2. 자식 프로그먼트에서 content 보여줄거임 (밑에 레이아웃 달아서 recyclerview 표시)

    val HUMOR = 1
    val BEST = 2
    val GUEST = 3

    // comu fragment
    var comuList = mutableListOf<ComuModel>()

    //comu set
    @SuppressLint("NotifyDataSetChanged")
    fun setContents(comuModelList: MutableList<ComuModel>) {
        comuList.clear()
        comuList.addAll(comuModelList)
        notifyDataSetChanged()
    }


    /** implementation */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comu_items, parent, false)
        return ComuHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comu = comuList[position]

        val viewHolder = holder as ComuHolder

        viewHolder.comuBinding.tvComentCount.text = comu.commentCount.toString() ?: "카운터없음"
        viewHolder.comuBinding.tvContentTitle.text = comu.title ?: "타이틀없음"
        viewHolder.comuBinding.tvNickname.text = comu.creator ?: "닉없음"
        viewHolder.comuBinding.tvUploadTime.text = comu.timestamp.toString() ?: "타임없음"

        //메세지 삭제

//        viewHolder.itemView.setOnLongClickListener {
//            msgClicked(message, position)
//            false
//        }
        viewHolder.comuBinding.executePendingBindings()

    }


    override fun getItemCount(): Int {
        return comuList.size
    }

    /** binding */
    inner class ComuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var comuBinding: ComuItemsBinding = ComuItemsBinding.bind(itemView)
    }

}



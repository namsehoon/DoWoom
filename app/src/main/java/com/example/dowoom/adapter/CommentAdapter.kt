package com.example.dowoom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.R
import com.example.dowoom.databinding.CommentItemsBinding
import com.example.dowoom.databinding.ComuItemsBinding
import com.example.dowoom.model.comunityModel.Comment
import com.example.dowoom.model.comunityModel.ComuModel

class CommentAdapter(val context: Context) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {


    /** 변수 */

    // comment
    var commentList = mutableListOf<Comment>()

    //comment set
    @SuppressLint("NotifyDataSetChanged")
    fun setComments(commentListMutable: MutableList<Comment>) {
        commentList.clear()
        commentList.addAll(commentListMutable)
        Log.d("abcd","recyclerview - set commentList is : $commentList")
        notifyDataSetChanged()
    }



    /** implementation */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comment_items, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder:CommentAdapter.CommentHolder, position: Int) {
        val comment = commentList[position]



//        viewHolder.comuBinding.tvComentCount.text = comu.commentCount.toString() ?: "카운터없음"
        holder.commentBinding.tvComment.text = comment.comment.toString()
        holder.commentBinding.tvCommenter.text = comment.nickname.toString()



        holder.commentBinding.executePendingBindings()



    }


    override fun getItemCount(): Int {
        return commentList.size
    }

    /** binding */
    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var commentBinding: CommentItemsBinding = CommentItemsBinding.bind(itemView)
    }



}



package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.R
import com.example.dowoom.Util.CustomAlertDialog
import com.example.dowoom.databinding.DeletemsgBinding
import com.example.dowoom.databinding.ItemTalkBinding
import com.example.dowoom.databinding.ReceivemsgItemBinding
import com.example.dowoom.databinding.SendmsgItemBinding
import com.example.dowoom.model.ChatRoom
import com.example.dowoom.model.Member
import com.example.dowoom.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.system.measureTimeMillis

class chatMsgAdatper(val context: Context,
                     val chatId:String,
                     val myUid:String,
                     val msgClicked:(Message) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    /** 변수 */

    //메세지
    var messages = mutableListOf<Message>()

    //메세지 set
    fun setMessage(messages: MutableList<Message>) {
        Log.d("abcd","messages is in adapter is : ${messages.toString()}")
        messages.clear()
        messages.addAll(messages)
        notifyDataSetChanged()
    }

    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2


    /** implementation */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            //보내는 메세지
            val view = LayoutInflater.from(context).inflate(R.layout.sendmsg_item,parent,false)
            SendMsgHolder(view)
        } else {
            //받은 메세지
            val view = LayoutInflater.from(context).inflate(R.layout.receivemsg_item,parent,false)
            receiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        //만약 내 uid와 message sender의 uid가 동일하다면 ITEM_SENT
        Log.d("abcd","message sender is : ${message.sender}")
        return if (myUid == message.sender) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        //todo : 이미지 처리
        //보내는이
        if (holder.javaClass == SendMsgHolder::class.java) {
            val viewHolder = holder as SendMsgHolder

            //일반
            viewHolder.sendBinding.msgSend.text = message.message
            //메세지 삭제
            viewHolder.itemView.setOnLongClickListener {

                msgClicked(message)
                false
            }
            // todo : what is it for?
            message.chatUid.let { it1 ->
               if (it1 != null) {
                   FirebaseDatabase.getInstance().reference.child("Message")
                       .child(myUid).child(chatId).child(it1).setValue(message)

               }
            }
            Log.d("abcd","gdgdgdgdg")
            viewHolder.sendBinding.executePendingBindings()
        } else {
            //받는이
            val viewHolder = holder as receiveMsgHolder

            viewHolder.receiveBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    /** binding */

    inner class SendMsgHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var sendBinding : SendmsgItemBinding = SendmsgItemBinding.bind(itemView)
    }


    inner class receiveMsgHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var receiveBinding : ReceivemsgItemBinding = ReceivemsgItemBinding.bind(itemView)
    }



}
package com.example.dowoom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.databinding.ReceivemsgItemBinding
import com.example.dowoom.databinding.SendmsgItemBinding
import com.example.dowoom.model.talkModel.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class chatMsgAdatper(val context: Context,
                     val msgClicked:(Message, position:Int) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    /** 변수 */

    //메세지
    var messages = mutableListOf<Message>()
    //todo : @string 만들어서 보안 관련 코드들 넣어두기
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    //메세지 set
    @SuppressLint("NotifyDataSetChanged")
    fun setMessage(messageList: MutableList<Message>) {
        messages.clear()
        messages.addAll(messageList)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.count())
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
            ReceiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val message = messages[position]
        //만약 내 uid와 message sender의 uid가 동일하다면 ITEM_SENT
        return if (user == message.sender) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        //보내는이
        if (holder.javaClass == SendMsgHolder::class.java) {
            val viewHolder = holder as SendMsgHolder

            //이미지 처리
            if (message.message.equals("photo")) {
                viewHolder.sendBinding.llMsgSend.visibility = View.GONE
                viewHolder.sendBinding.llImgSend.visibility = View.VISIBLE
                viewHolder.sendBinding.imgSend.visibility = View.VISIBLE

                storageRef.child(message.imageUrl!!).downloadUrl.addOnCompleteListener { task ->
                    if (task.isComplete) {
                        Glide.with(context)
                            .load(task.result) // 이미지를 로드
                            .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                            .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
                            .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
                            .into(viewHolder.sendBinding.imgSend) //이미지를 보여줄 view를 지정
                    }
                }


            }

            //메세지
            viewHolder.sendBinding.msgSend.text = message.message
            //메세지 삭제

            viewHolder.itemView.setOnLongClickListener {
                msgClicked(message, position)
                false
            }
            viewHolder.sendBinding.executePendingBindings()

        } else {
            /** 상대방  */
            val viewHolder = holder as ReceiveMsgHolder
            //일반
            viewHolder.receiveBinding.msgReceive.text = message.message

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


    inner class ReceiveMsgHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var receiveBinding : ReceivemsgItemBinding = ReceivemsgItemBinding.bind(itemView)
    }



}
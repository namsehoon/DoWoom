package com.example.dowoom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.Util.GlideApp
import com.example.dowoom.Util.TimeStampToDate
import com.example.dowoom.databinding.ReceivemsgItemBinding
import com.example.dowoom.databinding.SendmsgItemBinding
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.talkModel.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class chatMsgAdatper(val context: Context,
                     val msgClicked:(Message, position:Int) -> Unit, val profileImg:String?
): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    /** 변수 */

    //메세지
    var messages = mutableListOf<Message>()

    fun addMessage(message: Message) {
        messages.add(message) //메세지 추가
        messages.sortByDescending { it.date } //  날짜로 정렬 오름
        messages.reverse() // 뒤집기 한판 승!
        notifyItemInserted(messages.count()) //삽입 notify
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
        return if (user == message.from) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        val current = TimeStampToDate().getDate(message.date!!)

        //보내는이
        if (holder.javaClass == SendMsgHolder::class.java) {
            val viewHolder = holder as SendMsgHolder
            //현재 시간

            //이미지 처리
            if (message.message.equals("이미지")) {
                viewHolder.sendBinding.llMsgSend.visibility = View.GONE
                viewHolder.sendBinding.llImgSend.visibility = View.VISIBLE
                viewHolder.sendBinding.imgSend.visibility = View.VISIBLE
                viewHolder.sendBinding.tvTimestamp.visibility = View.GONE

                Glide.with(context)
                    .load(message.imageUrl) // 이미지를 로드
                    .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                    .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
                    .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
                    .into(viewHolder.sendBinding.imgSend) //이미지를 보여줄 view를 지정
            }

            //메세지
            viewHolder.sendBinding.msgSend.text = message.message

            //다음 메세지가 null이 아니고, 보낸이가 같다면

                if (position+1 < messages.count() && messages[position+1].to == message.to) {
                    val next = TimeStampToDate().getDate(messages[position+1].date!!)

                    //현재 메세지의 시간과 다음 메세지의 시간이 같다면,
                    if (current.equals(next)) {
                        if (message.message.equals("이미지")) {
                            viewHolder.sendBinding.tvImgTimestamp.text = ""
                        } else {
                            viewHolder.sendBinding.tvTimestamp.text = ""
                        }
                    } else {
                        if (message.message.equals("이미지")) {
                            viewHolder.sendBinding.tvImgTimestamp.text = current
                        } else {
                            viewHolder.sendBinding.tvTimestamp.text = current
                        }
                    }
                } else {
                    if (message.message.equals("이미지")) {
                        viewHolder.sendBinding.tvImgTimestamp.text = current
                    } else {
                        viewHolder.sendBinding.tvTimestamp.text = current
                    }
                }




            //메세지 삭제
            viewHolder.itemView.setOnLongClickListener {
                msgClicked(message, position)
                false
            }
            viewHolder.sendBinding.executePendingBindings()

        } else {
            /** 상대방  */
            val viewHolder = holder as ReceiveMsgHolder


            //이미지 처리
            if (message.message.equals("이미지")) {
                viewHolder.receiveBinding.llMsgReceive.visibility = View.GONE
                viewHolder.receiveBinding.llImgReceive.visibility = View.VISIBLE
                viewHolder.receiveBinding.imgReceive.visibility = View.VISIBLE
                viewHolder.receiveBinding.tvTimestamp.visibility = View.GONE

                Glide.with(context)
                    .load(message.imageUrl) // 이미지를 로드
                    .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                    .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
                    .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
                    .into(viewHolder.receiveBinding.imgReceive) //이미지를 보여줄 view를 지정
            }


            //상대방 프로필 사진
            if (profileImg.isNullOrEmpty()) {
                val res = R.drawable.ic_baseline_person_24
                viewHolder.receiveBinding.ivProfile.setImageDrawable(context.getDrawable(res))
            } else {
                GlideApp.with(context)
                    .load(profileImg) // 이미지를 로드
                    .override(80,80)
                    .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                    .error(R.drawable.ic_baseline_person_24) // 불러오다가 에러발생
                    .into(viewHolder.receiveBinding.ivProfile) //이미지를 보여줄 view를 지정
            }

            //다음 메세지가 null이 아니고, 보낸이가 같다면

                Log.d("Abcd","position : ${position+1}, messages : ${messages.count()}")
                if (position+1 < messages.count() && messages[position+1].from == message.from) {
                    val next = TimeStampToDate().getDate(messages[position+1].date!!)

                    //현재 메세지의 시간과 다음 메세지의 시간이 같다면,
                    if (current.equals(next)) {
                        if (message.message.equals("이미지")) {
                            viewHolder.receiveBinding.tvImgTimestamp.text = ""
                        } else {
                            viewHolder.receiveBinding.tvTimestamp.text = ""
                        }
                    } else {
                        if (message.message.equals("이미지")) {
                            viewHolder.receiveBinding.tvImgTimestamp.text = current
                        } else {
                            viewHolder.receiveBinding.tvTimestamp.text = current
                        }
                    }
                } else {
                    if (message.message.equals("이미지")) {
                        viewHolder.receiveBinding.tvImgTimestamp.text = current
                    } else {
                        viewHolder.receiveBinding.tvTimestamp.text = current
                    }
                }


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
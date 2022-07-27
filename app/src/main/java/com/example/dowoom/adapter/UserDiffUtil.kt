package com.example.dowoom.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.dowoom.model.User
import com.example.dowoom.model.comunityModel.Comment
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.model.talkModel.ChatRoom

// DiffUtil을 더 단순하게 사용할 수 있게 해주는 클래스다. 자체적으로 멀티 쓰레드에 대한 처리가 되어있기 때문에 개발자가 직접 동기화 처리를 할 필요가 없어진다.
class UserDiffUtil : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User) =
        oldItem.uid == newItem.uid

    override fun areContentsTheSame(oldItem: User, newItem: User) =
        oldItem.uid == newItem.uid
}


class ComuModelDiffUtil : DiffUtil.ItemCallback<ComuModel>() {

    override fun areItemsTheSame(oldItem: ComuModel, newItem: ComuModel) =
        oldItem.uid == newItem.uid

    override fun areContentsTheSame(oldItem: ComuModel, newItem: ComuModel) =
        oldItem.uid == newItem.uid
}

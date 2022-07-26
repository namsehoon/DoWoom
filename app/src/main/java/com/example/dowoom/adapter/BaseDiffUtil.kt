package com.example.dowoom.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.dowoom.model.User

// DiffUtil은 RecyclerView의 성능을 한층 더 개선할 수 있게 해주는 유틸리티 클래스다. 기존의 데이터 리스트와 교체할 데이터 리스트를 비교해서 실질적으로 업데이트가 필요한 아이템들을 추려낸다.
class BaseDiffUtil<T>(private val newList: List<T>, private val oldList: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    // areItemsTheSame() : 두 아이템이 동일한 아이템인지 체크한다. 예를들어 item이 자신만의 고유한 id 같은걸 가지고 있다면 그걸 기준으로 삼으면 된다.
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newList[newItemPosition] == oldList[oldItemPosition]

    // 두 아이템이 동일한 내용물을 가지고 있는지 체크한다. 이 메서드는 areItemsTheSame()가 true일 때만 호출된다.
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newList[newItemPosition] == oldList[oldItemPosition]
}

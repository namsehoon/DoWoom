package com.example.dowoom.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.User
import com.example.dowoom.repo.userRepo

class ShowProfileViewModel : ViewModel() {

    val repo = userRepo()

    /** 차단하기 */



    val _block = MutableLiveData<Boolean>()
    val block : LiveData<Boolean> get() = _block

    fun blockUser(partnerUid:String,blockedState:String) {
        repo.blockUser(partnerUid,blockedState).observeForever(Observer { result ->
            //false = 차단해제됨
            //true = 차단됨
            if (result != null) {
                _block.value = result
            }
        })
    }


    val _blockCheck = MutableLiveData<Boolean>()
    val blockCheck : LiveData<Boolean> get() = _blockCheck

    /** 차단 관찰 */
    fun observeBlock(partnerUid: String) {
        repo.observeBlock(partnerUid).observeForever(Observer {
            if (it != null) {
                _blockCheck.value = it
            }
        })
    }
    /** 차단 관찰 */
    val _iblockCheckYou = MutableLiveData<Boolean>()
    val iblockCheckYou : LiveData<Boolean> get() = _iblockCheckYou

    fun iobserveBlockedYou(partnerUid: String) {
        repo.iObserveBlockedYou(partnerUid).observeForever(Observer {
            if (it != null) {
                _iblockCheckYou.value = it
            }
        })
    }
}
package com.example.dowoom.viewmodel.mainViewmodel

import android.graphics.Insets.add
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.model.comunityModel.ContentModel
import com.example.dowoom.repo.ComuRepo
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ComuViewModel(private val repo:GezipRepo) : ViewModel() {


    val comuRepo = ComuRepo()

    val page = ObservableField<Int>(1)

//
//    init {
//       viewModelScope.launch {
//           getHumors()
//           getGuest()
//       }
//    }

    /** 프로세스(새로고침) */
    private val _progress = MutableLiveData<Boolean>(true)
    val progress: LiveData<Boolean>
        get() = _progress

    /** 유머게시판 */

    private val _comuList = MutableLiveData<MutableList<ComuModel>>()
    val comuList: LiveData<MutableList<ComuModel>>
        get() = _comuList

    suspend fun getHumors() {
        val data = repo.loadGezipNotice(1)
        data
            .onCompletion {
                _progress.value = false
                Log.d("abcd","ComuViewmodel - getHumors 로드 완료됨.")
            }
            .collect {
                _comuList.value = _comuList.value?.apply { add(it) } ?: mutableListOf(it)
            }

    }

    /** 익명게시판 */

    private val _guestList = MutableLiveData<MutableList<ComuModel>>()
    val guestList : LiveData<MutableList<ComuModel>>
        get() = _guestList

    fun getGuest() {//todo: 여기서 개수 처리 하는게 나을 듯
        viewModelScope.launch {
            _progress.value = true
            comuRepo.getGuestList().observeForever(Observer { result ->
                _guestList.value = result
                if (result == null) {
                    _progress.value = true
                }
                _progress.value= true
            })
        }
    }

    /** 익명게시판 콘텐츠 가져오기 */
    private val _guestContent = MutableLiveData<ContentModel>()
    val guestContent : LiveData<ContentModel>
        get() = _guestContent


    private val _getFirstContent = MutableLiveData<String>()
    val getFirstContent : LiveData<String>
        get() = _getFirstContent

    fun getGuestContent(uid:String) {
        viewModelScope.launch {
            comuRepo.getGuestContent(uid)
                .onCompletion { Log.d("abcd", "ComuViewmodel - getGuestContent 로드 완료됨.") }
                .catch { Log.d("abcd", "ComuViewmodel - getGuestContent error : ${it.message}") }
                .collect {
                    Log.d("abcd", "가져온 content is : ${it.contentText}")
                    _guestContent.value = it
                    _getFirstContent.value = it.contentText ?: ""
                }
        }
    }

}
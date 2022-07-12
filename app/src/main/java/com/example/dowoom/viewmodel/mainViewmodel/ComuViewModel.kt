package com.example.dowoom.viewmodel.mainViewmodel

import android.content.Context
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
import com.example.dowoom.model.comunityModel.Comment
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
    /** 유머 콘텐츠 */

    private val _comuContent = MutableLiveData<ComuModel>()
    val comuContent: LiveData<ComuModel>
        get() = _comuContent

    suspend fun getHumorContent(comuModel: ComuModel) {
        val data = repo.loadGezipContent(comuModel = comuModel)
        data
            .onCompletion {
                _progress.value = false
                Log.d("abcd","ComuViewmodel - getHumors 로드 완료됨.")
            }
            .collect {
                Log.d("abcd","viewmodel - getHumorContent : ${it} ")
                _comuContent.value = it
            }

    }



    /** 익명게시판 콘텐츠 */

    private val _guestList = MutableLiveData<MutableList<ComuModel>>()
    val guestList : LiveData<MutableList<ComuModel>>
        get() = _guestList

    fun getGuest() {//todo: 여기서 개수 처리 하는게 나을 듯
        viewModelScope.launch {
            _progress.value = false
            comuRepo.getGuestList().observeForever(Observer { result ->
                _guestList.value = result
                if (result == null) {
                    _progress.value = false
                }
                _progress.value= false
            })
        }
    }


    /** 댓글 리스트 */

    private val _commentList = MutableLiveData<MutableList<Comment>>()
    val commentList : LiveData<MutableList<Comment>>
        get() = _commentList

    fun getComments(comuModel: ComuModel) {
        viewModelScope.launch {
            comuRepo.getComments(comuModel).observeForever(Observer {
                _commentList.value = it
            })
        }
    }


    /**  댓글 추가  */
    fun insertComment(comuModelId: String, commentText: String) {
        viewModelScope.launch {
            comuRepo.insertCommentWriteIn(comuModelId, commentText)
        }
    }


}
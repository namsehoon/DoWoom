package com.example.dowoom.viewmodel.mainViewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.retrofit.GezipRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ComuViewModel(private val repo:GezipRepo) : ViewModel() {


    val page = ObservableField<Int>(1)


    init {
       viewModelScope.launch {
           getHumors()
       }
    }

    /** 유머게시판 */

    private val _comuList = MutableLiveData<MutableList<ComuModel>>()
    val comuList: LiveData<MutableList<ComuModel>>
        get() = _comuList

    suspend fun getHumors() {
        val data = repo.loadGezipNotice(1)
        data
            .onCompletion {
            Log.d("abcd","ComuViewmodel - getHumors 로드 완료됨.")
        }
            .collect {
                _comuList.value = _comuList.value?.apply { add(it) } ?: mutableListOf(it)
            }

    }



    /** 익명게시판 */
    fun getGuest() {
        viewModelScope.launch {

        }
    }
}
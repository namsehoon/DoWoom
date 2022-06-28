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


    private val _comuList = MutableLiveData<MutableList<ComuModel>>()
    val comuList: LiveData<MutableList<ComuModel>>
        get() = _comuList

    init {
        getHumors()
    }

    fun getHumors() {
        _comuList.value = mutableListOf()
        viewModelScope.launch {
            val data = repo.loadGezipNotice(1)
            data
                .onCompletion {
                //완료되면?
            }
                .collect {
                    _comuList.value = _comuList.value?.apply { add(it) } ?: mutableListOf(it)
                }
        }
    }
}
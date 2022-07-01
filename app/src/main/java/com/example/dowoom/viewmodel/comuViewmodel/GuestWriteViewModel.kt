package com.example.dowoom.viewmodel.comuViewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dowoom.repo.ComuRepo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class GuestWriteViewModel : ViewModel() {

    val repo = ComuRepo()

    val _result = MutableLiveData<Boolean>()
    val result : LiveData<Boolean> get() = _result


    fun insertGuestWriteIn(subject: String, content: String) {
        viewModelScope.launch {
            repo.insertGuestWriteIn(subject, content)
                .catch { e ->
                    Log.d("abcd","GuestWriteViewModel - insertGuestWriteIn - error : ${e.message}")
                }
                .onCompletion {
                    Log.d("abcd","GuestWriteViewModel - insertGuestWriteIn - onCompletion")
                }
                .collect { bool ->
                    _result.value = bool
                }
        }
    }
}
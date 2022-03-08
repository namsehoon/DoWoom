package com.example.dowoom.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseViewModel : ViewModel() {
    // 더 이상 Observable의 구독이 필요없을 때는 이를 폐기

    //1회용 리소스를 나타낸다
    private val compositeDisposable = CompositeDisposable()



    fun addDisposable(disposable:Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }



}
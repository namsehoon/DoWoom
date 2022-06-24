package com.example.dowoom.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel


//UNCHECKED_CAST : 형식 검사 없이 원시 유형을 매개 변수화된 유형에 캐스팅할 때 이 경고가 표시됩니다.
@Suppress("UNCHECKED_CAST") //???????
class ComuViewModelFactory(private val repo : GezipRepo) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ComuViewModel(repo) as T
    }
}
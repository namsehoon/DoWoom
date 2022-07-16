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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion

@ExperimentalCoroutinesApi
class ComuViewModel(private val repo:GezipRepo) : ViewModel() {


    val comuRepo = ComuRepo()

    var page = ObservableField<Int>(1)
    /** 다음 페이지 */
    fun nextPageBtnClicked() {
        page.set(page.get()?.plus(1))
        CoroutineScope(Dispatchers.Main).launch {
            getHumors() //유머리스트 가져오기
            delay(1000) //딜레이
        }
    }

    /** 이전 페이지 */
    fun beforePageBtnClicked() {
        if (page.get() != 1) {
            page.set(page.get()?.minus(1))
            CoroutineScope(Dispatchers.Main).launch {
                getHumors() //유머리스트 가져오기
                delay(1000) //딜레이
            }
        } else {
            Log.d("abcd","첫번째 페이지 입니다.")
        }
    }

    /** 다음 콘텐츠 */
    fun nextBtnClicked() {
        Log.d("abcd","다음버튼 클릭 됨")
        //현재 컨텐츠 리스트에 이 컨텐츠를 포함하고 있다면
        Log.d("Abcd","comu content is :${comuContent.value?.uid}")
        if (uidLists.contains(comuContent.value?.uid)) {
            //이 인덱스를 가져와서
            val index = uidLists.indexOf(comuContent.value?.uid)

            //다음 인덱스를 뿌려줄거임.. 만약 인덱스에 없으면
            if (uidLists.size <= index.plus(1)) {
                Log.d("abcd","여기까지")
                page.set(page.get()?.plus(1))

                _progress.value = true
                CoroutineScope(Dispatchers.Main).launch {
                    getHumors() //유머리스트 가져오기
                    delay(1000) //딜레이
                    val firstContent = comuList.value?.get(1) //두번째 콘텐츠
                    getHumorContent(firstContent!!) // 가져오기
                }

            } else {
               val nextContent = comuList.value?.get(index.plus(1))
                CoroutineScope(Dispatchers.Main).launch {
                    getHumorContent(nextContent!!)
                }
            }
        } else {
            Log.d("abcd","ComuViewModel - nextBtnClicked")
        }

    }
// 1. 1페이지로 넘어옴, 1페이지의 첫번째 인덱스임. 첫번째 인덱스와 1페이지라면, = 첫번째 페이지입니다.
    /** 이전 콘텐츠 */
    fun beforeBtnClicked() {
        Log.d("abcd","이전버튼 클릭 됨")
        //현재 컨텐츠 리스트에 이 컨텐츠를 포함하고 있다면
        if (uidLists.contains(comuContent.value?.uid)) {
            //이 인덱스를 가져와서
            val index = uidLists.indexOf(comuContent.value?.uid)

            //현재 인덱스 -1이 0이고, 1페이지 라면
            if (index.minus(1) == 0 && comuContent.value?.page?.toInt() == 1) {
                Log.d("abcd","첫번째 페이지 입니다.")

            } else if(index.minus(1) != 0) { // 앞에 콘텐츠가 더 있다면,

                //페이지의 마지막 콘텐츠
                val frontContent = comuList.value?.get(index.minus(1))
                CoroutineScope(Dispatchers.Main).launch {
                    getHumorContent(frontContent!!)
                }
            } else if(index.minus(1) == 0) {// 앞에 콘텐츠가 더 없다면,
                //페이지 -1
                page.set(page.get()?.minus(1))

                _progress.value = true
                //콘텐츠를 불러오기
                CoroutineScope(Dispatchers.Main).launch {
                    getHumors()
                    delay(1000)
                    //페이지의 마지막 콘텐츠
                    val lastContent = comuList.value?.last()
                    getHumorContent(lastContent!!)

                }

            }
        } else {
            Log.d("abcd","ComuViewModel - nextBtnClicked")
        }
    }

    /** 인덱스 */
    val uidLists:MutableList<String> = mutableListOf<String>()



    /** 프로세스(새로고침) */
    private val _progress = MutableLiveData<Boolean>(true)
    val progress: LiveData<Boolean>
        get() = _progress

    /** 유머게시판 콘텐츠 들*/

    private val _comuList = MutableLiveData<MutableList<ComuModel>>()
    val comuList: LiveData<MutableList<ComuModel>>
        get() = _comuList

    suspend fun getHumors() {
        uidLists.clear()
        Log.d("abcd","contentuid is : ${uidLists.toString()}")
        Log.d("abcd","getHumors - page is : ${page.get()}")
        val data = repo.loadGezipNotice(page.get()!!)
        data
            .onCompletion {
                _progress.value = false
                Log.d("abcd","ComuViewmodel - getHumors 로드 완료됨.")
            }
            .collect {
                _comuList.value = it
                Log.d("abcd","_comuList.value.toString() is  :${_comuList.value.toString()}")
               it.forEach { comuModel ->
                   uidLists.add(comuModel.uid!!)
               }
                Log.d("Abcd","contentuid after that : ${uidLists}")
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
                if (it != null) {
                    _comuContent.value = it
                }
            }

    }



    /** 익명게시판 콘텐츠 */

    private val _guestList = MutableLiveData<MutableList<ComuModel>>()
    val guestList : LiveData<MutableList<ComuModel>>
        get() = _guestList

    fun getGuest() {//todo: 여기서 개수 처리 하는게 나을 듯
        viewModelScope.launch {
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
    fun insertComment(comuModelId: String, commentText: String, password:String?) {
        viewModelScope.launch {
            if (password == null) {
                comuRepo.insertCommentWriteIn(comuModelId, commentText,null) //유머
            } else {
                comuRepo.insertCommentWriteIn(comuModelId, commentText, password) // 익명
            }
        }
    }


}
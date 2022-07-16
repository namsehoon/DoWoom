package com.example.dowoom.retrofit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.service.autofill.Dataset
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.model.comunityModel.ContentModel
import com.example.dowoom.model.comunityModel.Gezip
import com.example.dowoom.model.gameModel.GameModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import retrofit2.*
import java.lang.Exception
import java.net.URL
import java.util.concurrent.Flow


class GezipRepo {


    //해당 페이지에 대한 '유머 리스트 가져오기'
    fun loadGezipNotice(page:Int) : kotlinx.coroutines.flow.Flow<MutableList<ComuModel>> = flow {
        var count = 0
        val humorList = mutableListOf<ComuModel>()

        humorList.clear()
        //페이지 + get 요청
        val call = GezipClient().service.loadPage(page.toString())
        Log.d("abcd","loadGezipNotice - 유머리스트 가져오기")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d("abcd","실패")
                } else {

                    try {
                        val html = response.body()?.string()
                        val document = Jsoup.parse(html)

                        val list = document.select(".cnt_article_wrap > .cnt_lef_area > .board_cnt_wrap > table > tbody > tr")
                        for (content in list) {
                            val title = content.select("td.b_td2 > a").text()
                            val kindOf = 1
                            val creator = content.select("td.b_td3 > span").text()
                            val contentNumber = content.select("td.b_td1").text()

                            val comuModel = ComuModel(contentNumber,title,kindOf,null,creator,false,null,null,null,null,page.toString())
                            humorList.add(comuModel)

                        }

                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.d("abcd","성공")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("abcd","GezipRepo - loadGezipNotice - error - message : ${t.message}")
                Log.d("abcd","GezipRepo - loadGezipNotice - error - printStackTrace : ${t.printStackTrace()}")
                Log.d("abcd","GezipRepo - loadGezipNotice - error - localizedMessage: ${t.localizedMessage}")
            }


        })
        //todo: 계속 while문이 돌면 위험한디.. count 정해놔야함.. 사이트가 안들어가질 시.
        while (humorList.isEmpty()) {
            delay(1000)
            count += 1
            Log.d("abcd","humorList.isEmpty()")
            if (count == 5) {
                break
            }
        }
        //todo : ConcurrentModificationException
        //todo : ConcurrentModificationException
        //todo : ConcurrentModificationException

        emit(humorList)


    }.flowOn(Dispatchers.IO)



    //자료 가져오기
    fun loadGezipContent(comuModel: ComuModel)  : kotlinx.coroutines.flow.Flow<ComuModel?> = flow {
        var count = 0
        var updateComuModel:ComuModel? = null // imges 추가해줄
        val images: ArrayList<String> = ArrayList()
        //페이지 + get 요청
        val call = GezipClient().service.loadContent(comuModel.uid!!,comuModel.page!!) //요청

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d("abcd","실패")
                } else {

                    try {
                        val html = response.body()?.string()
                        val document = Jsoup.parse(html)
                        val contentData = document.select(".cnt_wrap > .cnt_article_wrap > .cnt_lef_area > .board_cnt_wrap > .board_view_wrap > img")
                        //각 이미지 소스 뽑기
                        Log.d("abcd","contentData : ${contentData}")
                        for (imgSrc in contentData) {
                            val url = imgSrc.attr("src")
                            if (url.startsWith("//")) {
                                images.add(url.split("fname=")[1])
                                Log.d("abc","loadGezipContent - edit url is : ${url.split("fname=")[1]}")
                            } else {
                                images.add(url)
                                Log.d("abc","loadGezipContent - nonEdit url is : ${url}")
                            }

                        }
                        updateComuModel = ComuModel(comuModel.uid,comuModel.title,comuModel.kindOf,comuModel.commentCount,comuModel.creator,comuModel.alreadySee,images,null,null,null,comuModel.page)

                    }catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("abcd","실패")
                    }

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("abcd","retrofit failed error : ${t.message}")
            }


        })
        while (updateComuModel?.contentImg == null) {
            delay(500) //todo : 고쳐야함
            Log.d("abcd","updateComuModel?.contentImg == null")
            count += 1
            if (count == 5) {
                break
            }
        }

        emit(updateComuModel)

    }.flowOn(Dispatchers.IO)
}

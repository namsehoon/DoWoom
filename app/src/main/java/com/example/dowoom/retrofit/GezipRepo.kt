package com.example.dowoom.retrofit

import android.service.autofill.Dataset
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dowoom.fragments.ComuFrag
import com.example.dowoom.fragments.childFragments.ComuHumor
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
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.Flow


//interface DataSource {
//    fun getComuList(page:Int) : kotlinx.coroutines.flow.Flow<ComuModel>
//}

class GezipRepo {

//    override fun getComuList(page:Int) : kotlinx.coroutines.flow.Flow<ComuModel> = flow<ComuModel> {
//
//        val results = loadGezipNotice(page)
//
//        for (result in results) {
//            emit(result)
//            Log.d("abcd", "getComuModels - results is : ${result.toString()}")
//        }
//
//
//    }.flowOn(Dispatchers.IO)


    //해당 페이지에 대한 '유머 리스트 가져오기'
    fun loadGezipNotice(page:Int) : kotlinx.coroutines.flow.Flow<ComuModel> = flow {

         val humorList = mutableListOf<ComuModel>()
            //페이지 + get 요청
            val call = GezipClient.service.loadPage(page.toString())
         Log.d("abcd","loadGezipNotice - 유머리스트 가져오기")

         call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d("abcd","실패")
                } else {

                    try {
                        val html = response.body()?.string()
                        val document = Jsoup.parse(html)

                        val contentData : Elements = document.select(".list-board").select("li")
                        for (content in contentData) {
                            if (!content.select(".member").get(0).text().equals("개집왕")) {
                                val title = content.select("a").first().ownText()
                                val kindOf = 1
                                val creator = content.select(".member").get(0).text()
                                val contentLocation = content.select(".item-subject").attr("href").replace("https://www.gezip.net/bbs/board.php?bo_table=realtime&wr_id=", "").split("&")[0]
                                val contentNumber = content.select(".wr-num").get(0).text()
//                                val timestamp = content.select(".wr-date").text() // 시간 필요하나?..
                                val comuModel = ComuModel(contentNumber,title,kindOf,null,creator,false,contentLocation)
                                humorList.add(comuModel)
                            }
                        }

                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.d("abcd","성공")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("abcd","retrofit failed error : ${t.message}")
            }


        })
        //todo: 계속 while문이 돌면 위험한디.. job/join?.. async/await?.
        while (humorList.isEmpty()) {
            delay(100)
            Log.d("abcd","humorList.isEmpty()")
        }
        for (h in humorList) {
            Log.d("Abcd","h is : ${h}")
            emit(h)
        }
    }

    //자료 가져오기
    fun loadGezipContent(content:String) {
        val humorList = mutableListOf<ContentModel>()
        //페이지 + get 요청
        val call = GezipClient.service.loadContent(content.toString())

        var comuModel:ComuModel? = null

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d("abcd","실패")
                } else {

                    try {
                        val html = response.body()?.string()
                        val document = Jsoup.parse(html)

                        Log.d("abcd","document is : ${document.text()}")

                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.d("abcd","성공")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("abcd","retrofit failed error : ${t.message}")
            }


        })

    }



}

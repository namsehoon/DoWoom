package com.example.dowoom.retrofit

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.lang.Exception
import java.util.concurrent.Flow


class GezipRepo {



    //해당 페이지에 대한 '유머 리스트 가져오기'
    fun loadGezipNotice(page:Int) : kotlinx.coroutines.flow.Flow<ComuModel> = flow {

         val humorList = mutableSetOf<ComuModel>()
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
                Log.d("abcd","GezipRepo - loadGezipNotice - error - message : ${t.message}")
                Log.d("abcd","GezipRepo - loadGezipNotice - error - printStackTrace : ${t.printStackTrace()}")
                Log.d("abcd","GezipRepo - loadGezipNotice - error - localizedMessage: ${t.localizedMessage}")
            }


        })
        //todo: 계속 while문이 돌면 위험한디.. count 정해놔야함.. 사이트가 안들어가질 시.
        while (humorList.isEmpty()) {
            delay(1000)
            Log.d("abcd","humorList.isEmpty()")
        }

        //todo : ConcurrentModificationException
        //todo : ConcurrentModificationException
        //todo : ConcurrentModificationException
        val itr:MutableIterator<ComuModel> = humorList.iterator()
        while (itr.hasNext()) {
            Log.d("Abcd","h is : ${itr.next()}")
            emit(itr.next())
        }
    }.flowOn(Dispatchers.IO)

    //자료 가져오기
    fun loadGezipContent(comuModel: ComuModel)  : kotlinx.coroutines.flow.Flow<ComuModel?> = flow {
        var updateComuModel:ComuModel? = null // imges 추가해줄
        val images: ArrayList<String> = ArrayList()
        //페이지 + get 요청
        val call = GezipClient.service.loadContent(comuModel.contentLocation!!) //요청

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d("abcd","실패")
                } else {

                    try {
                        var imageLocation:Elements? = null
                        val html = response.body()?.string()
                        val document = Jsoup.parse(html)

                        val contentData = document.select(".at-body").first()
                        //이미지들 위치
                        imageLocation = contentData.select(".view-wrap").select(".view-content").select(".img-tag")

                        //각 이미지 소스 뽑기
                        Log.d("abcd","imageloca : ${imageLocation}")
                        for (imgSrc in imageLocation) {
                            val url = imgSrc.absUrl("src").toString()
                            Log.d("abc","url is : ${imgSrc.absUrl("src")}")
                            images.add(url)
                            Log.d("abcd","images : ${images.toString()}")
                        }
                        updateComuModel = ComuModel(comuModel.uid,comuModel.title,comuModel.kindOf,comuModel.commentCount,comuModel.creator,comuModel.alreadySee,comuModel.contentLocation,images,null,null,null)

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
            delay(1000) //todo : 고쳐야함
            Log.d("abcd","updateComuModel?.contentImg == null")
        }
        emit(updateComuModel)

    }.flowOn(Dispatchers.IO)
}
//todo: 다른점 - src가 맞는듯? : https://www.gezip.net/data/editor/2207/08/thumb-1208928137_i36BKjUQ_a30a2cee09f4b487305f75a239e8aac42a35ec91_840x695.jpg
//todo: 다른점 : https://gezip.net/data/editor/2207/08/thumb-1208928137_i36BKjUQ_a30a2cee09f4b487305f75a239e8aac42a35ec91_840x695.jpg

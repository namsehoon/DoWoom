package com.example.dowoom.retrofit

import android.util.Log
import com.example.dowoom.fragments.ComuFrag
import com.example.dowoom.fragments.childFragments.ComuHumor
import com.example.dowoom.model.comunityModel.Gezip
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class GezipRepo {

    fun loadGezipNotice(page:Int, mCallback: ComuFrag) {
        //페이지 + get 요청
        val call = GezipClient.service.loadNotice(page.toString())

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
//                                val timestamp = content.select(".wr-date").text() // 시간 필요하나?..
                                Log.d("abcd","title is : ${title} , ${creator}, ${contentLocation}")
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
    }
}

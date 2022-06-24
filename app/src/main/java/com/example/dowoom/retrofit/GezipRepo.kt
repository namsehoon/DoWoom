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

                        val contentData : Elements = document.select(".list-body").select("li")
                        for (content in contentData) {
                            val title = content.select("a").first().ownText()
                            Log.d("abcd","content is : ${title}")
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

package com.example.dowoom.retrofit

import com.example.dowoom.model.comunityModel.Gezip
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//개집 크롤링 인터페이스
interface GezipService {
    // 리스트
    @GET("board.php?bo_table=humor2") //요청 : get, 필수 파라미터
    fun loadNotice(@Query("page") page:String) : Call<ResponseBody> //Call<> 안에 응답받을 Body 타입의 data class

    //콘텐츠
}
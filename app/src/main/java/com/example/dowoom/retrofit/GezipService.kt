package com.example.dowoom.retrofit

import com.example.dowoom.model.comunityModel.Gezip
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

//게임조선 크롤링 인터페이스
interface GezipService {

    // 유머 리스트
    @GET("board/list.php?bid=fun&cate=1") //요청 : get, 필수 파라미터
    fun loadPage(@Query("page") page:String) : Call<ResponseBody> //Call<> 안에 응답받을 Body 타입의 data class

    //콘텐츠
    @Headers("User-Agent: Android")
    @GET("board/view.php?bid=fun&cate=1")
    fun loadContent(@Query("num") content:String,@Query("page") page:String) : Call<ResponseBody>


}
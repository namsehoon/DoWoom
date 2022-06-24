package com.example.dowoom.retrofit

import retrofit2.Retrofit

object GezipClient {

    private const val baseUrl = "http://www.gezip.net/bbs/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .build()

    val service = retrofit.create(GezipService::class.java)

}
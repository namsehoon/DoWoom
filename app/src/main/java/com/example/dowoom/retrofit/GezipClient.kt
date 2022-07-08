package com.example.dowoom.retrofit

import retrofit2.Retrofit

object GezipClient {

    private const val baseUrl = "https://www.gezip.net"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .build()

    val service = retrofit.create(GezipService::class.java)

}

// https 무시 : .client(HandleHttps().getUnsafeOkHttpClient())

//https://gezip.net/data/editor/2207/08/2087684728_KAyuU5Gj_263543542b4b5ab00c535ebf645606d85a6ae052.jpg
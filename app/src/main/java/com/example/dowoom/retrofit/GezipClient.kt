package com.example.dowoom.retrofit

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.CookieManager
import javax.net.ssl.TrustManager

class GezipClient {

    private val baseUrl = "https://www.gamechosun.co.kr/webzine/"
    private val okttp = OkHttpClient()
        .newBuilder()
//        .cookieJar(JavaNetCookieJar(CookieManager())) //세션
//        .sslSocketFactory(SllAuth().getPinnedCertSslSocketFactory(context)!!)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okttp)
        .build()

    val service = retrofit.create(GezipService::class.java)

}

// https 무시 : .client(HandleHttps().getUnsafeOkHttpClient())

//https://gezip.net/data/editor/2207/08/2087684728_KAyuU5Gj_263543542b4b5ab00c535ebf645606d85a6ae052.jpg


package com.masary.yassin.masarypaymentapp.infrastructure

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient




/**
 * Created by yassin on 07/27/18.
 * MasaryRestServiceFactory
 */
class MasaryRestServiceFactory(private val baseUrl: String) {


//    private var httpClient = OkHttpClient()
    val service: MasaryRestService by lazy {
        Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(UnsafeOkHttpClientFactory.okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(
                        GsonBuilder()
                        .setLenient()
                        .create()
                ))
                .build()
                .create(MasaryRestService::class.java)
    }
//    val ServiceHelper:MasaryRestService by lazy {
//        createAdapter().build().create(MasaryRestService::class.java)
//    }

//    private fun ServiceHelper() :MasaryRestService{
//       return createAdapter().build().create(MasaryRestService::class.java)
//    }
//    private fun createAdapter(): Retrofit.Builder {
//
//        httpClient.readTimeoutMillis()
//        httpClient.connectTimeoutMillis()
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
//        httpClient.interceptors().add(interceptor)
//
//        return Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(httpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//    }
}
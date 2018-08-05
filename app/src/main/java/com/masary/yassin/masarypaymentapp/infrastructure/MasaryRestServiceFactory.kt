package com.masary.yassin.masarypaymentapp.infrastructure

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder


/**
 * Created by yassin on 07/27/18.
 * MasaryRestServiceFactory
 */
class MasaryRestServiceFactory(private val baseUrl: String) {

    val service: MasaryRestService by lazy {
        Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(UnsafeOkHttpClientFactory.okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                        .setLenient()
                        .create()
                ))
                .build()
                .create(MasaryRestService::class.java)
    }
}
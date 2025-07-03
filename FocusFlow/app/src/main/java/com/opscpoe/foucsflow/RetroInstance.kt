package com.opscpoe.foucsflow

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://zenquotes.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    //object class needs to pull the interface
    val someInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}
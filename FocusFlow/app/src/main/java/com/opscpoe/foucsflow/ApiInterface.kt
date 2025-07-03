package com.opscpoe.foucsflow

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    //site im getting the information from
    // https://zenquotes.io/api/random
    @GET("random")
    fun getData(): Call<List<MyDataItem>>
}
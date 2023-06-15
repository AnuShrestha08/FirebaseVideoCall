package com.anu.firebasevideocallingapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        private const val BASE_URL =  "https://fcm.googleapis.com/fcm/"

        fun  getClient(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }




    }



}
package com.kgg.android.seenear.network

import com.google.gson.GsonBuilder
import com.kgg.android.seenear.network.data.chatRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitChatInterface {

    companion object {

        private val BASE_URL = "http://34.28.253.42:9999"

        fun createForImportChat(): RetrofitChatInterface {

            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(40, TimeUnit.SECONDS)
//                .writeTimeout(40, TimeUnit.SECONDS)
//                .addInterceptor { chain -> chain.proceed(chain.request()) }
                .addInterceptor(interceptor)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .client(client)
//                .client(client)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(RetrofitChatInterface::class.java)
        }
    }

    //sms 발송
    @POST(".")
    fun sendChat(@Body contents: chatRequest): Call<String> // body X

}
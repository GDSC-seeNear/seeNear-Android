package com.kgg.android.seenear.network

import com.google.gson.GsonBuilder
import com.kgg.android.seenear.network.data.chatRequest
import com.kgg.android.seenear.network.data.chatResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ChatInterface {

    companion object {

        private val BASE_URL = "http://34.69.139.242:9999"

        fun createForImportChat(): ChatInterface {

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

            return retrofit.create(ChatInterface::class.java)
        }
    }

    //chat 발송
    @POST(".")
    fun sendChat(@Body contents: chatRequest): Call<String> // body X


}
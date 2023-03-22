package com.kgg.android.seenear.network

import com.google.gson.GsonBuilder
import com.kgg.android.seenear.network.Constants.Companion.BASE_URL
import com.kgg.android.seenear.network.data.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

interface RetrofitInterface {

    companion object {

        private val BASE_URL = "http://34.28.97.245:8080/"

        fun createForImport(): RetrofitInterface {

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

            return retrofit.create(RetrofitInterface::class.java)
        }
    }

    //sms 발송
    @POST("auth/send")
    fun sendSMS(@Body phoneNumber: String) : Call<String> // body X

    //코드 유효성 검사
    @POST("auth/check")
    fun checkVal(@Body checkValRequest: checkValRequest) : Call<checkValResponse> // signUpToken

    //보호자 회원가입
    @POST("auth/elderly/signup")
    fun userSignUp(
        @Header ("Authorization") authorizationHeader: String,
        @Body signupRequest: signupRequest) : Call<signupResponse>


    //보호자 회원가입
    @POST("auth/guardian/signup")
    fun adminSignUp(
        @Header ("Authorization") authorizationHeader: String,
        @Body signupRequest: signupRequest) : Call<signupResponse>

    //사용자 로그인
    @POST("auth/elderly/login")
    fun userLogIn(@Body loginRequest: loginRequest) : Call<loginResponse>

    //보호자 로그인
    @POST("auth/guardian/login")
    fun adminLogIn(@Body loginRequest: loginRequest) : Call<loginResponse>


    //토큰 테스트
    @GET("auth/c")
    fun tokenTest(
        @Header ("Authorization") authorizationHeader: String,
        ) : Call<String> // 해당 토큰 주인의 name

    //보호대상 등록 체크
    @POST("guardian/checkResister")
    fun checkResister(@Header ("Authorization") authorizationHeader: String,
                      @Body checkRegisterRequest: checkRegisterRequest) : Call<checkRegisterResponse> // signUpToken

    //보호대상 등록
    @PATCH("guardian/resister")
    fun register(@Header ("Authorization") authorizationHeader: String,
                 @Body checkRegisterRequest: registerRequest) : Call<registerResponse> // signUpToken

    // 보호자 본인 정보 가져오기
    @GET("guardian/me")
    fun myInfo_admin(
        @Header ("Authorization") authorizationHeader: String,
    ) : Call<myInfoResponse>

    // 사용자 본인 정보 가져오기
    @GET("elderly/me")
    fun myInfo_user(
        @Header ("Authorization") authorizationHeader: String,
    ) : Call<registerResponse>

    // 사용자 개인 정보 수정하기
    @PATCH("elderly/update")
    fun modify_myInfo(
        @Header ("Authorization") authorizationHeader: String,
        @Body checkRegisterRequest: registerResponse) : Call<registerResponse>

    // 보호하는 유저 리스트
    @GET("guardian/managedElderly")
    fun managedElderly(
        @Header ("Authorization") authorizationHeader: String,
        ) : Call<List<registerResponse>>


    // 보호 하는 유저 한명의 정보 가져오기
    @GET("guardian/managedElderly/{elderlyId}")
    fun getUserInfo(@Path("elderlyId") elderlyId: Int,
                    @Header ("Authorization") authorizationHeader: String): Call<registerResponse>


    // 약 등록
    @POST("medicine/create")
    fun medicineCreate(
        @Header ("Authorization") authorizationHeader: String,
        @Body medicineList : medicineCreate): Call<medicine>

    // 약 수정
    @PATCH("medicine/update/{medicineId}")
    fun medicineUpdate(
        @Path("medicineId") medicineId: Int,
        @Header ("Authorization") authorizationHeader: String,
        @Body medicine : medicine): Call<medicine>

    // elderly의 약 리스트 찾기
    @GET("medicine/getByElderlyId/{elderlyId}")
    fun medicineInquiry(
        @Path("elderlyId") elderlyId: Int,
        @Header ("Authorization") authorizationHeader: String, ) : Call<List<medicine>>

    // 약 삭제
    @DELETE("medicine/delete/{medicineId}")
    fun medicineDelete(
        @Path("medicineId") medicineId: Int,
        @Header ("Authorization") authorizationHeader: String, ) : Call<String>


}

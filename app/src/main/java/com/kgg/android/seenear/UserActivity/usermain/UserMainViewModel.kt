package com.kgg.android.seenear.UserActivity.usermain

import android.util.Log
import androidx.lifecycle.*
import com.kgg.android.seenear.App
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerResponse
import com.kgg.android.seenear.network.data.signupResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserMainViewModel(private val repository : RetrofitRepository) : ViewModel() {

    // 유저 정보 실시간 ui 업데이트를 위한 View Model & Live Observer

    val tokenResponse = MutableLiveData<registerResponse>()
    val tokenInfo = MutableLiveData<signupResponse>()

    fun tokenTest(accessToken: String) {


        viewModelScope.launch {
            Log.d("accessToken?", accessToken)

            val AuthorizationHeader = "Bearer " + accessToken
            val callApi = RetrofitInterface.createForImport().myInfo_user(AuthorizationHeader)
            callApi.enqueue(object : Callback<registerResponse> {
                override fun onResponse(call: Call<registerResponse>, response: Response<registerResponse>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            tokenResponse.value = it
                            Log.d("requestBody!!!", it.toString())
                        }
                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())

                        }
                    }
                }

                override fun onFailure(call: Call<registerResponse>, t: Throwable) {
                    Log.d("request Id in failure :", t.message.toString())
                }
            })

        }
    }

    fun refreshToken(accessToken: String) {


        viewModelScope.launch {
            Log.d("accessToken?", accessToken)

            val AuthorizationHeader = "Bearer " + accessToken
            val callApi = RetrofitInterface.createForImport().refreshToken(AuthorizationHeader)
            callApi.enqueue(object : Callback<signupResponse> {
                override fun onResponse(call: Call<signupResponse>, response: Response<signupResponse>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("requestBody!!!", it.toString())
                            tokenInfo.value = it
                            App.prefs.accessToken= it.accessToken
                            Log.d("accessToken :", App.prefs.accessToken.toString())

                        }
                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())

                        }
                    }
                }

                override fun onFailure(call: Call<signupResponse>, t: Throwable) {
                    Log.d("request Id in failure :", t.message.toString())
                }
            })

        }
    }

}
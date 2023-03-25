package com.kgg.android.seenear.AdminActivity.adminmain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgg.android.seenear.App
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.AuthorizationHeader
import com.kgg.android.seenear.network.data.myInfoResponse
import com.kgg.android.seenear.network.data.registerResponse
import com.kgg.android.seenear.network.data.signupResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMainViewModel (private val repository : RetrofitRepository) : ViewModel() {

    // 유저 정보 실시간 ui 업데이트를 위한 View Model & Live Observer

    var myName: MutableLiveData<String> = MutableLiveData()
    var userList: MutableLiveData<List<registerResponse>> = MutableLiveData()
    val tokenInfo = MutableLiveData<signupResponse>()

    fun myInfo(accessToken: String) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().myInfo_admin(AuthorizationHeader)
            callApi.enqueue(object : Callback<myInfoResponse> {
                override fun onResponse(call: Call<myInfoResponse>, response: Response<myInfoResponse>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("request Id in success :", it?.toString())
                            App.prefs.id = it.id
                            myName.value = it.name
                        }
                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<myInfoResponse>, t: Throwable) {
                    Log.d("request Id in failure :", t.message.toString())
                }
            })


        }
    }

    fun managedElderly(accessToken: String) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().managedElderly(AuthorizationHeader)
            callApi.enqueue(object : Callback<List<registerResponse>> {
                override fun onResponse(call: Call<List<registerResponse>>, response: Response<List<registerResponse>>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("request Id in success :", it?.toString())
                            userList.value = it
                        }
                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<List<registerResponse>>, t: Throwable) {
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
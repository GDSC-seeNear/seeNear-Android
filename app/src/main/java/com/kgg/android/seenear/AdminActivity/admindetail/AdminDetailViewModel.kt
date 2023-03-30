package com.kgg.android.seenear.AdminActivity.admindetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.utility.RetrofitUtil.apiService
import com.kgg.android.seenear.App
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDetailViewModel (private val repository : RetrofitRepository) : ViewModel() {

    // 유저 정보 실시간 ui 업데이트를 위한 View Model & Live Observer

    var userInfo: MutableLiveData<registerResponse> = MutableLiveData()
    var userList: MutableLiveData<List<Report>> = MutableLiveData()


    fun getUserInfo(accessToken: String, elderlyId : Int) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().getUserInfo( elderlyId, authorizationHeader = AuthorizationHeader )
            callApi.enqueue(object : Callback<registerResponse> {
                override fun onResponse(call: Call<registerResponse>, response: Response<registerResponse>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("request Id in success :", it?.toString())
                            userInfo.value = it
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

    // 유저 레포트 리스트
    fun getUserReports(accessToken: String, elderlyId : Int) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().getUserReports( authorizationHeader = AuthorizationHeader, elderlyId )
            Log.d("callAPi", callApi.toString())
            userList.value = callApi.reportList
//            callApi.enqueue(object : Callback<ReportResponse> {
//                override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
//                    if (response.isSuccessful()) { // <--> response.code == 200
//                        // 성공 처리
//                        response.body()?.let {
//                            Log.d("request Id in success :", response.code().toString())
//                            Log.d("request Id in success :", it?.toString())
//                            userList.value = it.reportList
//                        }
//
//                    } else { // code == 401
//                        // 실패 처리
//                        response.body()?.let {
//                            Log.d("request Id in not :", response.code().toString())
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
//                    Log.d("request Id in failure :", t.message.toString())
//                }
//            })


        }
    }

}
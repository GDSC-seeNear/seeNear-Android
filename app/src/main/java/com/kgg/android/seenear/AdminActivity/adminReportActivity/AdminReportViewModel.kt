package com.kgg.android.seenear.AdminActivity.adminReportActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.Report
import com.kgg.android.seenear.network.data.registerResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminReportViewModel (private val repository : RetrofitRepository) : ViewModel() {

    var ReportInfo: MutableLiveData<Report> = MutableLiveData()


    fun getUserReportByDate(accessToken: String, elderlyId : Int, date: String) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().getUserReportByDate(  authorizationHeader = AuthorizationHeader, elderlyId,date  )
            callApi.enqueue(object : Callback<Report> {
                override fun onResponse(call: Call<Report>, response: Response<Report>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("request Id in success :", it?.toString())
                            ReportInfo.value = it
                        }

                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<Report>, t: Throwable) {
                    Log.d("request Id in failure :", t.message.toString())
                }
            })


        }
    }


}
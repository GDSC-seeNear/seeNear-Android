package com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgg.android.seenear.App
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.AuthorizationHeader
import com.kgg.android.seenear.network.data.medicine
import com.kgg.android.seenear.network.data.myInfoResponse
import com.kgg.android.seenear.network.data.registerResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMedicineInquiryViewModel (private val repository : RetrofitRepository) : ViewModel() {

    // 유저 정보 실시간 ui 업데이트를 위한 View Model & Live Observer

    var myName: MutableLiveData<String> = MutableLiveData()
    var medicineList: MutableLiveData<List<medicine>> = MutableLiveData()

    fun medicineInquiry(accessToken: String, elderly_id : Int) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().medicineInquiry(elderlyId = elderly_id, AuthorizationHeader)
            callApi?.enqueue(object : Callback<List<medicine>> {
                override fun onResponse(call: Call<List<medicine>>, response: Response<List<medicine>>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("request Id in success :", it?.toString())
                            medicineList.value = it
                        }
                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<List<medicine>>, t: Throwable) {
                    Log.d("request Id in failure :", t.message.toString())
                }
            })


        }
    }

    fun medicineDelete(medicineId: Int, accessToken: String) {
        viewModelScope.launch {

            val AuthorizationHeader = "Bearer " + accessToken

            val callApi = RetrofitInterface.createForImport().medicineDelete(medicineId = medicineId, AuthorizationHeader)
            callApi?.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let {
                            Log.d("request Id in success :", response.code().toString())
                            Log.d("request Id in success :", it?.toString())
                        }
                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let {
                            Log.d("request Id in not :", response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("request Id in failure :", t.message.toString())
                }
            })


        }
    }
}
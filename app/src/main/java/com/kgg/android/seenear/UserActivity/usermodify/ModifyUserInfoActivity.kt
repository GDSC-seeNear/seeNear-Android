package com.kgg.android.seenear.UserActivity.usermodify

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.room.jarjarred.org.antlr.v4.runtime.misc.MurmurHash.finish
import com.google.android.gms.common.config.GservicesValue.value
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.SearchActivity
import com.kgg.android.seenear.AdminActivity.RegisterActivity.RegisterActivity2.Companion.fullAddress
import com.kgg.android.seenear.AdminActivity.admindetail.AdminDetailActivity
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.App
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.databinding.ActivityModifyUserInfoBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.AuthorizationHeader
import com.kgg.android.seenear.network.data.registerRequest
import com.kgg.android.seenear.network.data.registerResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ModifyUserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModifyUserInfoBinding
    private lateinit var viewModel : UserMainViewModel
    private lateinit var userInfo: registerResponse
    var elderlyId: Int = 0

    val REQUEST_CODE = 100

    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(App.prefs.role.equals("user"))
            userInfo = UserMainActivity.userInfo
        else
            userInfo = AdminDetailActivity.userInfo


        elderlyId = intent.getIntExtra("elderlyId", 0)

        binding.nameEdittext.text = userInfo.name
        binding.birthEdittext.text = userInfo.birth
        binding.homeDetailEdittext.hint = userInfo.addressDetail

        Log.d("userInfo", userInfo.emergencyPhoneNumber.toString())

        binding.homeEdittext.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE)

        }

        binding.homeEdittext.text = fullAddress

        binding.plusBtnText.setOnClickListener {
            Log.d("visibility check", (binding.familyLayout2.visibility == View.GONE).toString())
            if (binding.familyLayout2.visibility == View.GONE)
                binding.familyLayout2.visibility = View.VISIBLE
            else if (binding.familyLayout3.visibility == View.GONE){
                binding.familyLayout3.visibility = View.VISIBLE
                binding.plusBtn1.visibility = View.GONE
            }
        }

        // 유저 등록하기
        binding.registText.setOnClickListener {
            var phoneList : List<String> = listOf()
            if (binding.familyLayout2.visibility == View.GONE && binding.familyEdittext21.text.isNotEmpty())
                phoneList = listOf(binding.familyEdittext21.text.toString(),)
            else if (binding.familyLayout3.visibility == View.GONE && binding.familyEdittext22.text.isNotEmpty())
                phoneList = listOf(binding.familyEdittext21.text.toString(), binding.familyEdittext22.text.toString())
            else if (binding.familyLayout3.visibility == View.VISIBLE && binding.familyEdittext23.text.isNotEmpty())
                phoneList = listOf(binding.familyEdittext21.text.toString(), binding.familyEdittext21.text.toString(),  binding.familyEdittext23.text.toString())

            userInfo.emergencyPhoneNumber = phoneList
            userInfo.addressDetail = binding.homeDetailEdittext.text.toString()

            App.prefs.accessToken?.let { it1 -> modifyUserInfo(it1, userInfo) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            Toast.makeText(
                this,
                "주소 : ${data?.getStringExtra("address").toString()}",
                Toast.LENGTH_SHORT
            )
                .show()
            userInfo.addressLati = data?.getDoubleExtra("latitude", 0.0)
            userInfo.addressLongi = data?.getDoubleExtra("longitude", 0.0)
            binding.homeEdittext.text = data?.getStringExtra("address").toString()
        }

    }


    // 인증 메세지 전송
    fun modifyUserInfo(accessToken: String,  userInfo: registerResponse) {



        val AuthorizationHeader = "Bearer " + accessToken

        Log.d("accessToken?", AuthorizationHeader)

        val callApi = RetrofitInterface.createForImport().register(AuthorizationHeader, userInfo)
        callApi.enqueue(object : Callback<registerResponse> {
            override fun onResponse(call: Call<registerResponse>, response: Response<registerResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    response.body()?.let {
                        Log.d("request Id in success :", response.code().toString())
                        Log.d("requestBody!!!", it.toString())
                        finish()
//                        val intent = Intent(this@ModifyUserInfoActivity, AdminDetailActivity::class.java)
//                        intent.putExtra("elderlyId", elderlyId)
//                        startActivity(intent)
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
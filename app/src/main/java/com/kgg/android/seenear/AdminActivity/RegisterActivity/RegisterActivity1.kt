package com.kgg.android.seenear.AdminActivity.RegisterActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.SignupActivity
import com.kgg.android.seenear.databinding.ActivityRegister1Binding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity1 : AppCompatActivity() {

    // Admin의 User 등록 - 1

    private lateinit var binding: ActivityRegister1Binding


    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    companion object{
        var userInfo : checkRegisterResponse? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        // bottom sheet 설정
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)
        val userCheckBtn = bottomSheetDialog.findViewById<ImageView>(R.id.userCheckBtn)
        val userInfo_text = bottomSheetDialog.findViewById<TextView>(R.id.userInfo_text)

        // 인증 메세지 전송
        fun sendSMS(phoneNum : String) {

            Log.d("sendSMS?","phoneNum: " + phoneNum)

            val callApi = smsAuthApi.sendSMS(phoneNumber = phoneNum)
            callApi.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        Toast.makeText(this@RegisterActivity1, "인증번호를 전송하였습니다.", Toast.LENGTH_SHORT).show()

                    } else { // code == 401
                        // 실패 처리
                        response.body()?.let{
                            Log.d("request Id :", response.code().toString())

                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("request Id :", t.message.toString())
                }
            }
            )
        }

        // 보호대상 등록 체크
        fun checkResister(phoneNumber : String, certificationNumber : String) {

            Log.d("checkVal?","phoneNumber: " + phoneNumber)
            Log.d("checkVal?","certificationNumber: " + certificationNumber)

            val checkRegisterRequest = checkRegisterRequest(phoneNumber = phoneNumber, certificationNumber = certificationNumber)
            val AuthorizationHeader = "Bearer " + App.prefs.accessToken
            Log.d("checkVal?","AuthorizationHeader: " + AuthorizationHeader)

            val callApi = smsAuthApi.checkResister(AuthorizationHeader, checkRegisterRequest)
            callApi.enqueue(object : Callback<checkRegisterResponse> {
                override fun onResponse(call: Call<checkRegisterResponse>, response: Response<checkRegisterResponse>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        response.body()?.let{
                            Log.d("signUpToken :", it.name.toString())
                            bottomSheetDialog.show()
                            userInfo_text?.text = it.name.toString()
                            userInfo = it
                        }



                    } else { // code == 401
                        // 실패 처리
                        Log.d("onResponse else :", response.code().toString())
                        Log.d("onResponse else :", response.message().toString())

                    }
                }
                override fun onFailure(call: Call<checkRegisterResponse>, t: Throwable) {
                    Log.d("onFailure :", t.message.toString())

                }
            }
            )
        }

        binding.sendText.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                SignupActivity.phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "휴대폰 번호: "+ SignupActivity.phoneNum, Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "휴대폰 번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sendBtn.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                SignupActivity.phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "휴대폰 번호: "+ SignupActivity.phoneNum, Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "휴대폰 번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.registerBtn.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkResister(SignupActivity.phoneNum!!, binding.numberEdittext.text.toString())
            }
        }

        binding.registText.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkResister(SignupActivity.phoneNum!!, binding.numberEdittext.text.toString())
            }
        }


        userCheckBtn?.setOnClickListener {
            val intent = Intent(this, RegisterActivity2::class.java)
            startActivity(intent)
        }


    }



}
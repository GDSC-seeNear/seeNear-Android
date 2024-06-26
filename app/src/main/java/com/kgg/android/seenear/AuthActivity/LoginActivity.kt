package com.kgg.android.seenear.AuthActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.App
import com.kgg.android.seenear.SignupActivity
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.databinding.ActivityUserLoginBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.loginRequest
import com.kgg.android.seenear.network.data.loginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginBinding
    private lateinit var role: String


    companion object{
        var phoneNum : String ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        role = intent.getStringExtra("role").toString() // admin or user


        binding.signupText2.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.sendBtn.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                phoneNum = binding.phoneEdittext.text.toString()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
                Toast.makeText(this, "The verification code has been sent.", Toast.LENGTH_SHORT).show()

            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "Please enter your phone number accurately.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sendText.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "The verification code has been sent.", Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "Please enter your phone number accurately.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registText.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                logIn(phoneNum.toString(), binding.numberEdittext.text.toString(), role )
            }
        }

        binding.registerBtn.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                logIn(phoneNum.toString(), binding.numberEdittext.text.toString(), role )
            }
        }

    }
    // 인증 메세지 전송
    fun sendSMS(phoneNum : String) {

        Log.d("sendSMS?!","phoneNum: " + phoneNum)

        val callApi = RetrofitInterface.createForImport().sendSMS(phoneNumber = phoneNum)
        callApi.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리

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

    // 로그인
    public fun logIn(phoneNum : String,certificationNumber : String, role: String) {

        Log.d("logIn?","phoneNum: " + phoneNum)
        Log.d("logIn?","certificationNumber: " + certificationNumber)

        val loginRequest = loginRequest(phoneNum,  certificationNumber)
        var callApi = RetrofitInterface.createForImport().userLogIn(loginRequest)
        var intent = Intent(this@LoginActivity, UserMainActivity::class.java)

        if (role.equals("admin")){
            callApi = RetrofitInterface.createForImport().adminLogIn(loginRequest)
            intent = Intent(this@LoginActivity, AdminMainActivity::class.java)
        }

        callApi.enqueue(object : Callback<loginResponse> {
            override fun onResponse(call: Call<loginResponse>, response: Response<loginResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Toast.makeText(applicationContext, "You have been logged in.", Toast.LENGTH_SHORT).show()
                    response.body()?.let{
                        App.prefs.accessToken= it.accessToken
                        App.prefs.refreshToken= it.refreshToken
                        App.prefs.role = role
                        Log.d("accessToken :", App.prefs.accessToken.toString())
                        Log.d("refreshToken :", App.prefs.refreshToken.toString())
                        Log.d("role :", App.prefs.role.toString())

                    }

                    startActivity(intent)

                } else { // code == 401
                    // 실패 처리
                    Toast.makeText(applicationContext, "Login failed.", Toast.LENGTH_SHORT).show()
                    Log.d("accessToken :", response.code().toString())

                }
            }

            override fun onFailure(call: Call<loginResponse>, t: Throwable) {
                Log.d("accessToken :", t.message.toString())
            }
        }
        )
    }

}
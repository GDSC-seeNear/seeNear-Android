package com.kgg.android.seenear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kgg.android.seenear.AuthActivity.LoginActivity
import com.kgg.android.seenear.databinding.ActivityUserSignupBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.checkValRequest
import com.kgg.android.seenear.network.data.checkValResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSignupBinding
    private lateinit var role: String

    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    companion object{
        var phoneNum : String ?= null
        var signUpToken : String ?= null
        var checkValRequest : checkValRequest? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        role = intent.getStringExtra("role").toString() // "user" or "admin"

        if (role.equals("user")){
            binding.phoneText2.text = "User Sign Up"
            binding.loginText2.text = "User Log In"
        }
        else {
            binding.phoneText2.text = "Caregiver Sign Up"
            binding.loginText2.text = "Caregiver Log In"
        }

        binding.loginText2.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }

        binding.sendText.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                phoneNum = binding.phoneEdittext.text.toString()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "Please enter your phone number accurately.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sendBtn.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                phoneNum = binding.phoneEdittext.text.toString()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "Please enter your phone number accurately.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.registerBtn.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkVal(phoneNum!!, binding.numberEdittext.text.toString())
            }
        }

        binding.registText.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkVal(phoneNum!!, binding.numberEdittext.text.toString())
            }
        }




    }

    // 인증 메세지 전송
    fun sendSMS(phoneNum : String) {

        Log.d("sendSMS?","phoneNum: " + phoneNum)

        val callApi = smsAuthApi.sendSMS(phoneNumber = phoneNum)
        callApi.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Log.d("request Id :", response.code().toString())
                    Toast.makeText(this@SignupActivity, "The verification code has been sent.", Toast.LENGTH_SHORT).show()

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

    // 코드 유효성 검사
    fun checkVal(phoneNumber : String, certificationNumber : String) {

        Log.d("checkVal?","phoneNumber: " + phoneNumber)
        Log.d("checkVal?","certificationNumber: " + certificationNumber)


        checkValRequest = checkValRequest(phoneNumber = phoneNumber, certificationNumber = certificationNumber)
        val callApi = smsAuthApi.checkVal(checkValRequest!!)
        callApi.enqueue(object : Callback<checkValResponse> {
            override fun onResponse(call: Call<checkValResponse>, response: Response<checkValResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    response.body()?.let{
                        Log.d("signUpToken :", it.signUpToken.toString())
                        signUpToken = it.signUpToken.toString()
                    }
                    val intent = Intent(this@SignupActivity, SignupActivity2::class.java)
                    intent.putExtra("phoneNum", phoneNum)
                    intent.putExtra("role", role)
                    startActivity(intent)
                } else { // code == 401
                    // 실패 처리
                    Log.d("onFailure :", "else")

                }
            }
            override fun onFailure(call: Call<checkValResponse>, t: Throwable) {
                Log.d("onFailure :", "onFailure")

            }
        }
        )
    }




}
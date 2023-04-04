package com.kgg.android.seenear

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kgg.android.seenear.AdminActivity.RegisterActivity.RegisterActivity2
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.AuthActivity.IntroActivity
import com.kgg.android.seenear.AuthActivity.LoginActivity
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.databinding.ActivityUserSignup2Binding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity2 : AppCompatActivity(){

    private lateinit var binding: ActivityUserSignup2Binding
    private lateinit var role: String

    private var phoneNum: String ?= null
    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSignup2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        role = intent.getStringExtra("role").toString()
        phoneNum = intent.getStringExtra("phoneNum")

        if (role.equals("user")){
            binding.phoneText4.text = "User Sign Up"
        }
        else {
            binding.phoneText4.text = "Caregiver Sign Up"
        }

        binding.registerBtn.setOnClickListener {
            if (binding.nameEdittext.text.length >= 2){
                signUp(binding.nameEdittext.text.toString(), phoneNum!!)
            }
            else
                Toast.makeText(this, "Please enter your name accurately.", Toast.LENGTH_SHORT).show()
        }

        binding.registText.setOnClickListener {
            if (binding.nameEdittext.text.length >= 2){
                signUp(binding.nameEdittext.text.toString(), phoneNum!!)
            }
            else
                Toast.makeText(this, "Please enter your name accurately.", Toast.LENGTH_SHORT).show()
        }
    }

    // 회원가입
    fun signUp(name : String, phoneNum : String) {

        Log.d("signup?","name: " + name)
        Log.d("signup?","phoneNum: " + phoneNum)
        Log.d("signup?","signUpToken: " + SignupActivity.signUpToken)
        val AuthorizationHeader = "Bearer " + SignupActivity.signUpToken

        val signupRequest = signupRequest(name, phoneNum)
        var callApi = smsAuthApi.userSignUp(AuthorizationHeader, signupRequest)

        if (role.equals("admin"))
            callApi = smsAuthApi.adminSignUp(AuthorizationHeader, signupRequest)



        callApi.enqueue(object : Callback<signupResponse> {
            override fun onResponse(call: Call<signupResponse>, response: Response<signupResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200

                    Toast.makeText(this@SignupActivity2, "You have successfully signed up.", Toast.LENGTH_SHORT).show()

                    response.body()?.let{
                        Log.d("signup?", "accesstoken: " + it.accessToken.toString())
                        Log.d("signup?", "refreshtoken: " + it.refreshToken.toString())
                        App.prefs.accessToken = it.accessToken.toString()
                        App.prefs.refreshToken = it.refreshToken.toString()
                    }
                    val intent = Intent(this@SignupActivity2, IntroActivity::class.java)
                    startActivity(intent)

                    // 성공 처리

                    // 자동 로그인
                    if (SignupActivity.checkValRequest!= null){
                        SignupActivity.checkValRequest!!.certificationNumber?.let {
                            SignupActivity.checkValRequest!!.phoneNumber?.let { it1 ->
                                logIn(it1,
                                    it, role)
                            }
                        }
                    }
                }
                else if (response.code() == 409){ // user is already existed, 자동 로그인 진행
                    if (SignupActivity.checkValRequest!= null){
                        SignupActivity.checkValRequest!!.certificationNumber?.let {
                            SignupActivity.checkValRequest!!.phoneNumber?.let { it1 ->
                                logIn(it1,
                                    it, role)
                            }
                        }
                    }

                }
                else {

                    var stringToJson = JSONObject(response.errorBody()?.string()!!)
                    Log.e("signup?", "error: ${stringToJson.getString("error")}")

                    Toast.makeText(this@SignupActivity2, stringToJson.getString("error"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignupActivity2, IntroActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<signupResponse>, t: Throwable) {

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
        var intent = Intent(this, UserMainActivity::class.java)

        if (role.equals("admin")){
            callApi = RetrofitInterface.createForImport().adminLogIn(loginRequest)
            intent = Intent(this, AdminMainActivity::class.java)
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
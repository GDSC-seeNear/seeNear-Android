package com.kgg.android.seenear.AdminActivity.RegisterActivity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.SearchActivity
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.App
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.databinding.ActivityRegister2Binding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.registerRequest
import com.kgg.android.seenear.network.data.registerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDateTime
import java.util.*


class RegisterActivity2 : AppCompatActivity() {

    // Admin의 User 등록 - 2

    private lateinit var binding: ActivityRegister2Binding
    private lateinit var userInfo : registerResponse


    val REQUEST_CODE = 100

    companion object{
        var fullAddress : String = "주소를 선택해주세요"
    }
    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfo = registerResponse(
            RegisterActivity1.userInfo?.id,
            RegisterActivity1.userInfo?.phoneNumber,
            RegisterActivity1.userInfo?.name,
            RegisterActivity1.userInfo?.birth,
            RegisterActivity1.userInfo?.addressLati,
            RegisterActivity1.userInfo?.addressLongi,
            RegisterActivity1.userInfo?.addressDetail,
            RegisterActivity1.userInfo?.isConnect,
            App.prefs.id
        )

        binding.nameEdittext.text = userInfo.name

        binding.birthEdittext.setOnClickListener {
            datepicker()
        }

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

            register()
        }


    }

    override fun onResume() {
        super.onResume()





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            userInfo.addressLati = data?.getDoubleExtra("latitude", 0.0)
            userInfo.addressLongi = data?.getDoubleExtra("longitude", 0.0)
            binding.homeEdittext.text = data?.getStringExtra("address").toString()
        }

    }


    fun datepicker(){

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val date_listener =
                DatePickerDialog.OnDateSetListener { p0, year, month, dayOfMonth ->
                    if(month<9&&dayOfMonth<10) {
                        binding.birthEdittext.text = "${year}-0${month + 1}-0${dayOfMonth}"
                        userInfo.birth =binding.birthEdittext.text.toString()

                    }
                    else if ( month<9&&dayOfMonth>=10) {
                        binding.birthEdittext.text = "${year}-0${month + 1}-${dayOfMonth}"
                        userInfo.birth =binding.birthEdittext.text.toString()

                    }
                    else if ( month>=9 && dayOfMonth<10) {
                        binding.birthEdittext.text = "${year}-${month + 1}-0${dayOfMonth}"
                        userInfo.birth =binding.birthEdittext.text.toString()

                    }
                    else {
                        binding.birthEdittext.text = "${year}-${month + 1}-${dayOfMonth}"
                        userInfo.birth =binding.birthEdittext.text.toString()

                    }
                }
            val builder = DatePickerDialog(this, date_listener, year, month, day).apply {
                datePicker.maxDate = System.currentTimeMillis()}
            builder.show()


    }

    // 인증 메세지 전송
    fun register() {


        val AuthorizationHeader = "Bearer " + App.prefs.accessToken
        Log.d("AuthorizationHeader :", AuthorizationHeader.toString())
        Log.d("userInfo :", userInfo.toString())
        userInfo.isConnect = true

        val callApi = smsAuthApi.register(authorizationHeader = AuthorizationHeader, checkRegisterRequest =  userInfo)
        callApi.enqueue(object : Callback<registerResponse> {
            override fun onResponse(call: Call<registerResponse>, response: Response<registerResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Toast.makeText(this@RegisterActivity2, "대상자 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    response.body()?.let{
                        Log.d("request Id :", it.toString())
                    }
                    val intent = Intent(this@RegisterActivity2, AdminMainActivity::class.java)
                    startActivity(intent)
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let{
                        Log.d("failureRequest :", response.code().toString())

                    }
                }
            }

            override fun onFailure(call: Call<registerResponse>, t: Throwable) {
                Log.d("failureRequest :", t.message.toString())
                if (t is HttpException) {
                    val errorBody = t.response()?.errorBody()?.string()
                    // errorBody에 저장된 에러메시지를 사용하여 적절한 처리를 수행합니다.
                    Log.d("errorBody", errorBody.toString())
                }

            }
        }
        )
    }

}
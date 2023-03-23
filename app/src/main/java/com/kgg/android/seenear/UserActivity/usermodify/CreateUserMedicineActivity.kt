package com.kgg.android.seenear.UserActivity.usermodify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kgg.android.seenear.App
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.AdminMedicineInquiryActivity
import com.kgg.android.seenear.databinding.ActivityModifyUserMedicineBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.medicine
import com.kgg.android.seenear.network.data.medicineCreate
import com.kgg.android.seenear.network.data.registerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class CreateUserMedicineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModifyUserMedicineBinding
    private lateinit var viewModel : UserMainViewModel
    private lateinit var userInfo: registerResponse
    private lateinit var medicine: medicineCreate
    private var elderlyId: Int = 0

    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyUserMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        elderlyId = intent.getIntExtra("elderlyId",0)
        medicine = medicineCreate()
        medicine.period = 24

        val adapter = ArrayAdapter.createFromResource(this, com.kgg.android.seenear.R.array.period, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.medicineSpinner11.adapter = adapter

        binding.medicineSpinner11.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // 선택된 항목 처리
                Log.e("selectedItem", position.toString())
                when(position){
                    0 -> medicine.period = 24 // 1일 1회
                    1 -> medicine.period = 12 // 1일 2회
                    2 -> medicine.period = 8 // 1일 3회
                    3 -> medicine.period = 24 * 2 // 2일 1회
                    4 -> medicine.period = 24 * 3 // 3일 1회
                    5 -> medicine.period = 24 * 7 // 7일 1회
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때 처리
            }
        })



        binding.plusBtn1.setOnClickListener {
            if (binding.medicineLayout2.visibility!= View.VISIBLE && binding.medicineEdittext1.text.length>0){ // 2번 약이 등록되지 않았을 때
                binding.medicineLayout2.visibility= View.VISIBLE
            }
            else if (binding.medicineLayout3.visibility!= View.VISIBLE  && binding.medicineEdittext2.text.length>0){ // 3번 약이 등록되지 않았을 때
                binding.medicineLayout3.visibility= View.VISIBLE
                binding.plusBtn1.visibility = View.INVISIBLE

            }
        }

        // 유저 등록하기
        binding.registText.setOnClickListener {

            if (binding.medicineEdittext1.text!!.length>0){
                medicine.elderlyId = elderlyId
                medicine.name = binding.medicineEdittext1.text!!.toString()
                Log.e("medicine!!!", medicine.toString())
                register(medicine)
            }
        }

    }



    // 인증 메세지 전송
    fun register( medicine : medicineCreate) {


        val AuthorizationHeader = "Bearer " + App.prefs.refreshToken
        Log.d("AuthorizationHeader :", AuthorizationHeader.toString())
        val callApi = smsAuthApi.medicineCreate(authorizationHeader = AuthorizationHeader, medicine)
        callApi.enqueue(object : Callback<medicine> {
            override fun onResponse(call: Call< medicine>, response: Response< medicine>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Toast.makeText(this@CreateUserMedicineActivity, "약 복용 정보 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    response.body()?.let{
                        Log.d("request Id :", it.toString())
                    }
                    val intent = Intent(this@CreateUserMedicineActivity, AdminMedicineInquiryActivity::class.java)
                    startActivity(intent)
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let{
                        Log.d("failureRequest :", response.code().toString())

                    }
                }
            }

            override fun onFailure(call: Call< medicine>, t: Throwable) {
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
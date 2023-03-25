package com.kgg.android.seenear.UserActivity.usermodify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kgg.android.seenear.AdminActivity.admindetail.AdminDetailViewModel
import com.kgg.android.seenear.App
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.MedicineInquiryActivity
import com.kgg.android.seenear.databinding.ActivityMedicineModifyBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.medicine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class MedicineModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicineModifyBinding
    private lateinit var viewModel : AdminDetailViewModel
    private lateinit var medicine: medicine
    private var period: Int = 0
    private var day: Int = 0
    private var time: Int = 0

    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.medicineEdittext1.text = MedicineInquiryActivity.medicine.name

        medicine = MedicineInquiryActivity.medicine
        period = medicine.period

        Log.e("period", period.toString())





        val adapter = ArrayAdapter.createFromResource(this, com.kgg.android.seenear.R.array.period, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.medicineSpinner11.adapter = adapter

        when(period){
            24 -> binding.medicineSpinner11.setSelection(0)
            12 -> binding.medicineSpinner11.setSelection(1)
            8 -> binding.medicineSpinner11.setSelection(2)
            24 * 2 -> binding.medicineSpinner11.setSelection(3)
            24 * 3 -> binding.medicineSpinner11.setSelection(4)
            24 * 7 -> binding.medicineSpinner11.setSelection(5)
        }

        binding.medicineSpinner11.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // 선택된 항목 처리
//                binding.medicineSpinnerText11.text = selectedItem

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




        medicine.period = 48

        binding.registText.setOnClickListener {

            medicineUpdate(medicine)
        }

    }


    // 인증 메세지 전송
    fun medicineUpdate( medicine : medicine ) {


        val AuthorizationHeader = "Bearer " + App.prefs.accessToken
        Log.d("AuthorizationHeader :", AuthorizationHeader.toString())
        val callApi = smsAuthApi.medicineUpdate(medicine.id, authorizationHeader = AuthorizationHeader, medicine = medicine)
        callApi.enqueue(object : Callback<medicine> {
            override fun onResponse(call: Call<medicine>, response: Response<medicine>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Toast.makeText(this@MedicineModifyActivity, "약 복용 정보 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    response.body()?.let{
                        Log.d("request Id :", it.toString())
                    }

                    finish()
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let{
                        Log.d("failureRequest :", response.code().toString())

                    }
                }
            }

            override fun onFailure(call: Call<medicine>, t: Throwable) {
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
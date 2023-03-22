package com.kgg.android.seenear.UserActivity.usermain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.App
import com.kgg.android.seenear.AuthActivity.IntroActivity
import com.kgg.android.seenear.AuthActivity.LoginActivity
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserInfoActivity
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserMedicineActivity
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.MedicineInquiryActivity
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerRequest
import com.kgg.android.seenear.network.data.registerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : UserMainViewModel

    companion object{
        var userInfo : registerResponse = registerResponse()
    }

    // View에 실시간 정보를 담기 위한 MVVM 패턴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = RetrofitRepository()
        val viewModelFactory = UserMainViewModelFactory(repository)

        viewModel = ViewModelProvider(this,viewModelFactory).get(UserMainViewModel::class.java)


        viewModel.tokenResponse.observe(this, Observer { it ->
            if (it != null){
                userInfo = it
                App.prefs.id = it.id
                binding.userNameText.text = it.name + " 님의"
            }

        })

        App.prefs.refreshToken?.let { viewModel.tokenTest(it) }

        // 개인정보 수정

        binding.modifyPersonalInfoBtn.setOnClickListener {
            val intent = Intent(this, ModifyUserInfoActivity::class.java)
            intent.putExtra("name", userInfo.name)
            startActivity(intent)
        }

        binding.modifyPersonalMedicineBtn.setOnClickListener {
            val intent = Intent(this, MedicineInquiryActivity::class.java)
            intent.putExtra("name", userInfo.name)
            startActivity(intent)
        }

    }

    private var lastTimeBackPressed : Long = 0

    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500) {
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else{
            finishAffinity()
        }
    }



}
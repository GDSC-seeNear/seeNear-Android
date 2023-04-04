package com.kgg.android.seenear.UserActivity.usermain

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.App
import com.kgg.android.seenear.AuthActivity.IntroActivity
import com.kgg.android.seenear.R
import com.kgg.android.seenear.UserActivity.userchat.UserChatActivity
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserInfoActivity
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.MedicineInquiryActivity
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerResponse
import java.util.*

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

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = String.format("%04d-%02d-%02d", year, month, day)

        viewModel = ViewModelProvider(this,viewModelFactory).get(UserMainViewModel::class.java)

        val dialog = LoadingDialog(this)

        viewModel.tokenResponse.observe(this, Observer { it ->
            if (it.name != null){
                userInfo = it
                App.prefs.id = it.id
                binding.userNameText.text = it.name + "?"
                dialog.dismiss()
            }
            else{
                App.prefs.refreshToken?.let { it1 -> viewModel.refreshToken(it1) }
                dialog.show() // token을 refresh하기 위한 로딩 구현
            }

        })

        viewModel.tokenInfo.observe(this, Observer {
            App.prefs.accessToken?.let { viewModel.tokenTest(it) }
            dialog.dismiss()
        })


        App.prefs.accessToken?.let { viewModel.tokenTest(it) }

        // 개인정보 수정

        binding.modifyPersonalInfoBtn.setOnClickListener {
            val intent = Intent(this, ModifyUserInfoActivity::class.java)
            intent.putExtra("name", userInfo.name)
            startActivity(intent)
        }

        // 챗봇 정보 수정

        binding.modifyPersonalMedicineBtn.setOnClickListener {
            val intent = Intent(this, MedicineInquiryActivity::class.java)
            intent.putExtra("name", userInfo.name)
            intent.putExtra("elderlyId", userInfo.id)
            startActivity(intent)
        }

        // 챗봇 버튼

        binding.chatbotBtn.setOnClickListener {
            val intent = Intent(this, UserChatActivity::class.java)
            intent.putExtra("name", userInfo.name)
            intent.putExtra("elderlyId", userInfo.id)
            startActivity(intent)
        }

        // 로그아웃

        binding.logoutBtn.setOnClickListener {
            App.prefs.accessToken = null
            App.prefs.refreshToken = null
            App.prefs.role = null
            App.prefs.id = null
            val intent = Intent(this, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

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


    class LoadingDialog(context: Context) : Dialog(context){

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_loading)

            // 취소 불가능
            setCancelable(false)

            // 배경 투명하게 바꿔줌
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }


    }
}
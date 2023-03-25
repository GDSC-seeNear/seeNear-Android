package com.kgg.android.seenear.UserActivity.usermain

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.UserActivity.userchat.UserChatActivity
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserInfoActivity
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.MedicineInquiryActivity
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerResponse

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

        val dialog = LoadingDialog(this)

        viewModel.tokenResponse.observe(this, Observer { it ->
            if (it.name != null){
                userInfo = it
                App.prefs.id = it.id
                binding.userNameText.text = it.name + " 님의"
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


        // 테스트용 만료된 accessToken
        // eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzZWVOZWFyIiwiaWF0IjoxNjc5NzI5MDc1LCJleHAiOjE2Nzk3MzI2NzUsImlkIjo3LCJ1dWlkIjoiMWYzNTM2NjMtZjY2NC00OTgwLWFiMjEtZTRmOGRiNmE2YzQ5Iiwicm9sZSI6IkVMREVSTFkifQ.wsa2T0Ja8Grd0YOX3kcffP18Y1KWrfe23RnveJArRfk

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
package com.kgg.android.seenear.AdminActivity.admindetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModel
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModelFactory
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityAdminDetailBinding
import com.kgg.android.seenear.databinding.ActivityAdminMainBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerResponse
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserInfoActivity
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.MedicineInquiryActivity


class AdminDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDetailBinding
    private lateinit var viewModel : AdminDetailViewModel
    private var elderly_id : Int = 0

    companion object{
        var userInfo : registerResponse = registerResponse()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)

        elderly_id = intent.getIntExtra("elderlyId", 0)
        var name = ""

        setContentView(binding.root)

        Log.d("lifecycle!!", "onCreate")

        val repository = RetrofitRepository()
        val viewModelFactory = AdminDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminDetailViewModel::class.java)


        viewModel.userInfo.observe(this, Observer {
            Log.d("userInfo",it.toString())
            binding.profileNameDetail.text = it.name
            binding.profileBirthDetail.text = it.birth
            binding.profileAddressDetail.text = it.addressDetail
            elderly_id = it.id!!
            name = it.name!!
            userInfo = it
        })

        App.prefs.accessToken?.let { viewModel.getUserInfo(it, elderly_id) }


        // 유저 개인정보 수정

        binding.userInfoModify.setOnClickListener {
            val intent = Intent(this, ModifyUserInfoActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("elderlyId", elderly_id)
            startActivity(intent)
        }

        // 유저 챗봇정보 수정

        binding.userChatbotModify.setOnClickListener {
            val intent = Intent(this, MedicineInquiryActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("elderlyId", elderly_id)
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle!!", "onResume")
        App.prefs.accessToken?.let { viewModel.getUserInfo(it, elderly_id) }
    }
}
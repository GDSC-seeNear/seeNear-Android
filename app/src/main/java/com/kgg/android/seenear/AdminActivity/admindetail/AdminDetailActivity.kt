package com.kgg.android.seenear.AdminActivity.admindetail

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


class AdminDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDetailBinding
    private lateinit var viewModel : AdminDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = RetrofitRepository()
        val viewModelFactory = AdminDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminDetailViewModel::class.java)


        viewModel.userInfo.observe(this, Observer {
            Log.d("userInfo",it.toString())
            binding.profileNameDetail.text = it.name
            binding.profileBirthDetail.text = it.birth
            binding.profileAddressDetail.text = it.addressDetail
        })

        App.prefs.refreshToken?.let { viewModel.getUserInfo(it, intent.getIntExtra("elderlyId", 0)) }


    }
}
package com.kgg.android.seenear.AdminActivity.admindetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModel
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.network.RetrofitRepository

class AdminDetailViewModelFactory (
    private val repository : RetrofitRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminDetailViewModel(repository) as T
    }
}
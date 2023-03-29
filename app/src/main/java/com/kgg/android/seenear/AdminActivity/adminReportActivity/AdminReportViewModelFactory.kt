package com.kgg.android.seenear.AdminActivity.adminReportActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModel
import com.kgg.android.seenear.network.RetrofitRepository

class AdminReportViewModelFactory (
    private val repository : RetrofitRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminReportViewModel(repository) as T
        }
    }
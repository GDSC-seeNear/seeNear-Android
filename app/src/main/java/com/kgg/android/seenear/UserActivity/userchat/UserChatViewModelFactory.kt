package com.kgg.android.seenear.UserActivity.userchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.network.RetrofitRepository

class UserChatViewModelFactory (
    private val repository : RetrofitRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserChatViewModel(repository) as T
    }
}
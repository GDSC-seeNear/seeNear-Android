package com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.network.RetrofitRepository

class AdminMedicineInquiryViewModelFactory (
    private val repository : RetrofitRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminMedicineInquiryViewModel(repository) as T
    }
}
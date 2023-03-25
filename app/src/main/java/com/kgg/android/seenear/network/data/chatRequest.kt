package com.kgg.android.seenear.network.data

data class chatRequest(
    var is_user_send : Boolean = true,
    var content : String? = null,
    var elderly_id : Int = 0
)

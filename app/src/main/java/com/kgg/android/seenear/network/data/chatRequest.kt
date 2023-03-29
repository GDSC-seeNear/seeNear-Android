package com.kgg.android.seenear.network.data

data class chatRequest(
    var elderlyId : Int = 0,
    var content : String? = null,
    var userSend : Boolean = true,
    var type : String? = null
)

package com.kgg.android.seenear.network.data

data class statusCheckRequest(
    var type : String? = null,
    var done : Boolean = false,
    var chatId : Int = 0
)

data class statusCheckResponse(
    var type : String? = null,
    var done : Boolean = false,
    var chatId : Int = 0
)

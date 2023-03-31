package com.kgg.android.seenear.network.data

data class chat(
    var id : Int = 0,
    var elderlyId : Int = 0,
    var content : String? = null,
    var createdAt : String? = null,
    var userSend : Boolean = true,
    var type : String? = null,
)

data class chatResponse(
    var chatList : List<chat>
)